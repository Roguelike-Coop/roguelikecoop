package io.github.roguelikecoop.roguelikecoop;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

class Language {
    public static final String DEFAULT = "/languages/en_US.xml";

    private HashMap<String, String> stringTable;

    private Language () {
        stringTable = new HashMap<String, String>();
    }

    public String getString (String key) {
        if (key == null) {
            return null;
        }
        return stringTable.get(key);
    }

    public static Language loadResource (String name) {
        assert name != null;

        try {
            Handler handler = new Handler(name);
            XMLReader reader = XMLReaderFactory.createXMLReader();

            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.setFeature("http://xml.org/sax/features/namespaces", false);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            reader.parse(new InputSource(Language.class.getResourceAsStream(name)));

            return handler.lang;
        } catch (IOException e) {
            throw new DataFileException(name, e);
        } catch (SAXException e) {
            throw new DataFileException(name, e);
        }
    }

    /** Handler for loading a Language from XML. */
    private static class Handler extends DefaultHandler {
        private enum Mode { ROOT, LANGUAGE, STRINGS, STRING };

        private Language lang;
        private String fileName;
        private Locator locator;
        private Mode mode;
        private String currentKey;
        private StringBuilder currentValue;

        Handler (String fileName) {
            lang = new Language();
            this.fileName = fileName;
            mode = Mode.ROOT;
        }

        @Override
        public void characters (char[] ch, int start, int length) {
            switch (mode) {
                case STRING:
                    if (currentValue == null) {
                        currentValue = new StringBuilder();
                    }
                    currentValue.append(ch, start, length);
                    break;

                default:
                    for (int i = 0; i < length; i++) {
                        if (!Character.isWhitespace(ch[start + i])) {
                            throwError("Unexpected text.");
                        }
                    }
                    break;
            }
        }

        @Override
        public void endElement (String uri, String localName, String qName) {
            switch (mode) {
                case LANGUAGE:
                    mode = Mode.ROOT;
                    break;

                case STRINGS:
                    mode = Mode.LANGUAGE;
                    break;

                case STRING:
                    if (currentValue == null) {
                        lang.stringTable.put(currentKey, null);
                    } else {
                        lang.stringTable.put(currentKey, currentValue.toString());
                    }

                    mode = Mode.STRINGS;
                    currentKey = null;
                    currentValue = null;
                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void error (SAXParseException e) {
            throwError(e);
        }

        @Override
        public void fatalError (SAXParseException e) {
            throwError(e);
        }

        @Override
        public void setDocumentLocator (Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement (String uri,
                                  String localName,
                                  String qName,
                                  Attributes attr) {

            switch (mode) {
                case ROOT:
                    if (!qName.equals("language")) {
                        throwError("Expected root element <language>.");
                    }
                    mode = Mode.LANGUAGE;
                    break;

                case LANGUAGE:
                    if (!qName.equals("strings")) {
                        throwError("Expected element <strings>.");
                    }
                    mode = Mode.STRINGS;
                    break;

                case STRINGS:
                    if (!qName.equals("string")) {
                        throwError("Expected element <string>.");
                    }

                    currentKey = attr.getValue("key");

                    if (currentKey == null) {
                        throwError("Expected attribute 'key'.");
                    } else if (lang.stringTable.containsKey(currentKey)) {
                        throwError("Duplicate key '" + currentKey + "'.");
                    }

                    mode = Mode.STRING;
                    break;

                case STRING:
                    throwError("Unexpected element <" + qName + ">.");
                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        private void throwError (String detail) {
            if (locator == null) {
                throw new DataFileException(fileName, detail);
            } else {
                throw new DataFileException(fileName,
                                            locator.getLineNumber(),
                                            locator.getColumnNumber(),
                                            detail);
            }
        }

        private void throwError (Throwable cause) {
            if (locator == null) {
                throw new DataFileException(fileName, cause);
            } else {
                throw new DataFileException(fileName,
                                            locator.getLineNumber(),
                                            locator.getColumnNumber(),
                                            cause);
            }
        }
    }
}
