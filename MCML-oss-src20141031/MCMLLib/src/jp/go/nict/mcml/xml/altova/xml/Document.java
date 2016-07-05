/**
 * Document.java
 *
 * This file was generated by XMLSPY 2004 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSPY Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package jp.go.nict.mcml.xml.altova.xml;

import java.util.*;


public abstract class Document implements java.io.Serializable {
    protected static javax.xml.parsers.DocumentBuilderFactory factory = null;
    protected static javax.xml.parsers.DocumentBuilder builder = null;
    protected static int tmpNameCounter = 0;

    protected static org.w3c.dom.Document tmpDocument = null;
    protected static org.w3c.dom.DocumentFragment tmpFragment = null;
    protected static java.util.Hashtable tmpFragmentTable = new Hashtable(); //kim
    protected static synchronized javax.xml.parsers.DocumentBuilder
            getDomBuilder() {
        try {
            if (builder == null) {
                if (factory == null) {
                    factory = javax.xml.parsers.DocumentBuilderFactory.
                              newInstance();
                    factory.setIgnoringElementContentWhitespace(true);
                    factory.setNamespaceAware(true);
                    //factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
                    //factory.setValidating(true);
                }
                builder = factory.newDocumentBuilder();

                builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                    public void warning(org.xml.sax.SAXParseException e) {
                    }

                    public void error(org.xml.sax.SAXParseException e) throws
                            XmlException {
                        throw new XmlException(e);
                    }

                    public void fatalError(org.xml.sax.SAXParseException e) throws
                            XmlException {
                        throw new XmlException(e);
                    }
                });
            }
            return builder;
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new XmlException(e);
        }
    }

    protected static synchronized org.w3c.dom.Document getTemporaryDocument() {
        if (tmpDocument == null) {
            tmpDocument = getDomBuilder().newDocument();
        }
        return tmpDocument;
    }

     protected static synchronized org.w3c.dom.Node createTemporaryDomNode() {
      String tmpName = "_" + tmpNameCounter++;
      if (tmpFragment == null) {
       tmpFragment = getTemporaryDocument().createDocumentFragment();
       tmpDocument.appendChild(tmpFragment);
      }
      org.w3c.dom.Node node = getTemporaryDocument().createElement(tmpName);
      tmpFragment.appendChild(node);
      tmpFragmentTable.put(tmpName, node);
      return node;
     }

    //Remove from tmpFragment kim
    protected static synchronized org.w3c.dom.Node removeTemporaryDomNode(org.
            w3c.dom.Node node) {
        org.w3c.dom.NodeList elements = tmpFragment.getChildNodes();
        String name = node.getNodeName() == null ? "" : node.getNodeName();

        org.w3c.dom.Node child = (org.w3c.dom.Node) tmpFragmentTable.get(name);
        if (child != null) {
            tmpFragmentTable.remove(name);
            return tmpFragment.removeChild(child);
        }
        return null;
    }

    //Remove from tmpFragment kim by name
    protected static synchronized org.w3c.dom.Node removeTemporaryDomNode(String sName) {
        org.w3c.dom.Node child = (org.w3c.dom.Node) tmpFragmentTable.get(sName);
        if (child != null) {
            tmpFragmentTable.remove(sName);
            return tmpFragment.removeChild(child);
        }
        return null;
    }


    protected String encoding = "UTF-8";
    protected String rootElementName = null;
    protected String namespaceURI = null;
    protected String schemaLocation = null;

    public Document() {
    }

    public synchronized void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public synchronized void setRootElementName(String namespaceURI, String rootElementName) {
        this.namespaceURI = namespaceURI;
        this.rootElementName = rootElementName;
    }

    public synchronized void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public synchronized org.w3c.dom.Node load(String filename) {
        try {
            return getDomBuilder().parse(new java.io.File(filename)).
                    getDocumentElement();
        } catch (org.xml.sax.SAXException e) {
            throw new XmlException(e);
        } catch (java.io.IOException e) {
            throw new XmlException(e);
        }
    }

    public synchronized org.w3c.dom.Node load(java.io.InputStream istream) {
        try {
            return getDomBuilder().parse(istream).getDocumentElement();
        } catch (org.xml.sax.SAXException e) {
            throw new XmlException(e);
        } catch (java.io.IOException e) {
            throw new XmlException(e);
        }
    }

    public synchronized  void save(String filename, Node node) {
        finalizeRootElement(node);

        Node.internalAdjustPrefix(node.domNode, true);
        node.adjustPrefix();

        internalSave(
                new javax.xml.transform.stream.StreamResult(
                        new java.io.File(filename)
                ),
                node.domNode.getOwnerDocument(),
                encoding
                );
    }

    public synchronized void save(java.io.OutputStream ostream, Node node) {
        finalizeRootElement(node);

        Node.internalAdjustPrefix(node.domNode, true);
        node.adjustPrefix();

        internalSave(
                new javax.xml.transform.stream.StreamResult(ostream),
                node.domNode.getOwnerDocument(),
                encoding
                );
    }

    protected synchronized static void internalSave(javax.xml.transform.Result result,
                                       org.w3c.dom.Document doc,
                                       String encoding) {
        try {
            javax.xml.transform.Source source
                    = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.Transformer transformer
                    = javax.xml.transform.TransformerFactory.newInstance().
                      newTransformer();
            if (encoding != null) {
                transformer.setOutputProperty("encoding", encoding);
            }
            transformer.transform(source, result);
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            throw new XmlException(e);
        } catch (javax.xml.transform.TransformerException e) {
            throw new XmlException(e);
        }
    }

    public synchronized org.w3c.dom.Node transform(Node node, String xslFilename) {
        try {
            javax.xml.transform.TransformerFactory factory = javax.xml.
                    transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = factory.
                    newTransformer(
                            new javax.xml.transform.stream.StreamSource(xslFilename)
                    );

            javax.xml.transform.dom.DOMResult result = new javax.xml.transform.
                    dom.DOMResult();
            transformer.transform(
                    new javax.xml.transform.dom.DOMSource(node.domNode),
                    result
                    );

            return result.getNode();
        } catch (javax.xml.transform.TransformerException e) {
            throw new XmlException(e);
        }
    }

    protected synchronized void finalizeRootElement(Node root) {
        if (root.domNode.getParentNode().getNodeType() !=
            org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE) {
            return;
        }

        if (rootElementName == null || rootElementName.equals("")) {
            throw new XmlException("Call setRootElementName first");
        }

        org.w3c.dom.Document doc = getDomBuilder().newDocument();
        org.w3c.dom.Element newRootElement = doc.createElementNS(namespaceURI,
                rootElementName);
        root.cloneInto(newRootElement);
        doc.appendChild(newRootElement);

        newRootElement.setAttribute("xmlns:xsi",
                                    "http://www.w3.org/2001/XMLSchema-instance");
        if (namespaceURI == null || namespaceURI.equals("")) {
            if (schemaLocation != null && schemaLocation != "") {
                newRootElement.setAttribute("xsi:noNamespaceSchemaLocation",
                                            schemaLocation);
            }
        } else {
            if (schemaLocation != null && schemaLocation != "") {
                newRootElement.setAttribute("xsi:schemaLocation",
                                            namespaceURI + " " + schemaLocation);
            }
        }

        root.domNode = newRootElement;
        declareNamespaces(root);
    }

    public abstract void declareNamespaces(Node node);

    protected synchronized void declareNamespace(Node node, String prefix, String URI) {
        node.declareNamespace(prefix, URI);
    }

    /*
            public void setRootElement(String namespaceURI, String rootElementName,Node node) {
                    setRootElementName(namespaceURI,rootElementName);
                    finalizeRootElement(node);

                    Node.internalAdjustPrefix(node.domNode, true);
                    node.adjustPrefix();

     }
     */
    public synchronized void setRootElement(String namespaceURI, String rootElementName, Node node) {
        String sTempName = node.domNode.getNodeName(); //kim add tmpFragment memory leak
        setRootElementName(namespaceURI, rootElementName);
        finalizeRootElement(node);

        Node.internalAdjustPrefix(node.domNode, true);
        node.adjustPrefix();
        Document.removeTemporaryDomNode(sTempName); //kim add tmpFragment memory leak

    }

}