package org.nuxeo.ecm.platform.shibboleth.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.platform.shibboleth.web.tree.UserTreeNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 */
public class TestUserTreeNodeParsing {

    @Test
    public void testParsing() {
        DocumentModel doc1 = new DocumentModelImpl("doc1", "type", "hello:world:john", null,null, null, null, null);
        DocumentModel doc2 = new DocumentModelImpl("doc2", "type", "hello:world:john2", null,null, null, null, null);

        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        docs.add(doc1);
        docs.add(doc2);

        List<UserTreeNode> nodes = UserTreeNode.getHierarcicalNodes(docs);
        assertEquals(1, nodes.size());
        assertEquals("hello", nodes.get(0).getId());
        assertEquals(1, nodes.get(0).getChildrens().size());
        assertEquals("world", nodes.get(0).getChildrens().get(0).getId());
        assertEquals(2, nodes.get(0).getChildrens().get(0).getChildrens().size());
        assertEquals("hello:world:john2", nodes.get(0).getChildrens().get(0).getChildrens().get(1).getId());
    }

    @Test
    public void testMixedGroup() {
        DocumentModel doc1 = new DocumentModelImpl("doc1", "type", "Administrators", null,null, null, null, null);
        DocumentModel doc2 = new DocumentModelImpl("doc1", "type", "Administrator:Admini", null,null, null, null, null);
        DocumentModel doc3 = new DocumentModelImpl("doc1", "type", "Administrator:Admini:hello", null,null, null, null, null);

        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);

        List<UserTreeNode> nodes = UserTreeNode.getHierarcicalNodes(docs);
        assertEquals(2, nodes.size());
        assertEquals("Administrators", nodes.get(0).getId());
        assertEquals("Administrator", nodes.get(1).getId());
        assertEquals(2, nodes.get(1).getChildrens().size());
        assertEquals("Administrator:Admini", nodes.get(1).getChildrens().get(0).getId());
        assertEquals("Admini", nodes.get(1).getChildrens().get(1).getId());
        assertEquals(1, nodes.get(1).getChildrens().get(1).getChildrens().size());
    }

    @Test
    public void testBuildBranch() {
        DocumentModel doc1 = new DocumentModelImpl("doc1", "type", "test", null,null, null, null, null);
        DocumentModel doc2 = new DocumentModelImpl("doc1", "type", "test:test", null,null, null, null, null);

        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        docs.add(doc1);
        docs.add(doc2);

        List<UserTreeNode> nodes = UserTreeNode.buildBranch("branch:hello", docs);
        assertEquals(1, nodes.size());
        assertEquals(1, nodes.get(0).getChildrens().size());
        assertEquals(2, nodes.get(0).getChildrens().get(0).getChildrens().size());

        nodes = UserTreeNode.buildBranch("branch:hello", new ArrayList<DocumentModel>());
        assertEquals(0, nodes.size());
    }

    @Test
    public void testParsingHandling() {
        DocumentModel doc1 = new DocumentModelImpl("doc1", "type", "empty", null,null, null, null, null);
        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        docs.add(doc1);

        List<UserTreeNode> nodes = UserTreeNode.getHierarcicalNodes(docs);
        assertEquals(1, nodes.size());
        assertEquals("empty", nodes.get(0).getId());

        doc1 = new DocumentModelImpl("doc1", "type", ":hello:", null,null, null, null, null);
        docs = new ArrayList<DocumentModel>();
        docs.add(doc1);

        nodes = UserTreeNode.getHierarcicalNodes(docs);
        assertEquals(1, nodes.size());
        assertEquals("", nodes.get(0).getId());
        assertEquals(1, nodes.get(0).getChildrens().size());
        assertEquals("hello", nodes.get(0).getChildrens().get(0).getId());
        assertEquals(1, nodes.get(0).getChildrens().get(0).getChildrens().size());
    }

    @Test
    public void testDisplayedName() {
        assertEquals("node", new UserTreeNode("node").getDisplayedName());
        assertNotSame("node", new UserTreeNode("node2").getDisplayedName());

        DocumentModel doc1 = new DocumentModelImpl("doc1", "type", "empty", null,null, null, null, null);
        assertEquals("empty", new UserTreeNode(doc1).getDisplayedName());

        doc1 = new DocumentModelImpl("doc1", "type", "empty:hello", null,null, null, null, null);
        assertEquals("hello", new UserTreeNode(doc1).getDisplayedName());

        doc1 = new DocumentModelImpl("doc1", "type", "empty:hello:", null,null, null, null, null);
        assertEquals("", new UserTreeNode(doc1).getDisplayedName());
    }
}
