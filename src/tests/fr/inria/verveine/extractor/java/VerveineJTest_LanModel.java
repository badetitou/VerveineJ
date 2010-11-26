/**
 * Copyright (c) 2010 Simon Denier
 */
package tests.fr.inria.verveine.extractor.java;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.Access;
import fr.inria.verveine.core.gen.famix.Attribute;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.Comment;
import fr.inria.verveine.core.gen.famix.FileAnchor;
import fr.inria.verveine.core.gen.famix.Inheritance;
import fr.inria.verveine.core.gen.famix.Invocation;
import fr.inria.verveine.core.gen.famix.LocalVariable;
import fr.inria.verveine.core.gen.famix.Method;
import fr.inria.verveine.core.gen.famix.Namespace;
import fr.inria.verveine.core.gen.famix.Parameter;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.SourceAnchor;
import fr.inria.verveine.extractor.java.JavaDictionary;
import fr.inria.verveine.extractor.java.VerveineJParser;

/**
 * @author Simon Denier
 * @since May 28, 2010
 *
 */
public class VerveineJTest_LanModel {

	private static final String A_CLASS_NAME = "--aClassName--";

	private Repository repo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		VerveineJParser parser = new VerveineJParser();
		parser.compile(new String[] {"test_src/LANModel"});
		parser.renameNamespaces();
		repo = parser.getFamixRepo();
	}

	@Test
	public void testEntitiesNumber() {
		assertEquals(11+8, TestVerveineUtils.selectElementsOfType(repo,fr.inria.verveine.core.gen.famix.Class.class).size()); // 11 + {Object,String,StringBuffer,AbstractStringBuilder,PrintStream,FilterOutputStream,OutputStream,System}
		assertEquals(3, TestVerveineUtils.selectElementsOfType(repo,PrimitiveType.class).size());//int,boolean,void
		assertEquals(40+7, TestVerveineUtils.selectElementsOfType(repo,Method.class).size());//40+{System.out.println(),System.out.println(...),System.out.print,StringBuffer.append,Object.equals,String.equals,Object.toString}
		assertEquals(10+1, TestVerveineUtils.selectElementsOfType(repo,Attribute.class).size());//10+{System.out}
		assertEquals(2+4, TestVerveineUtils.selectElementsOfType(repo,Namespace.class).size());//2+{moose,java.lang,java.io,java}
		assertEquals(26, TestVerveineUtils.selectElementsOfType(repo,Parameter.class).size());
		assertEquals(54, TestVerveineUtils.selectElementsOfType(repo,Invocation.class).size());//actually 54, missing 2 !!!!
		assertEquals(5+12, TestVerveineUtils.selectElementsOfType(repo,Inheritance.class).size());//5 internal + 12 from imported packages/classes
		assertEquals(25, TestVerveineUtils.selectElementsOfType(repo,Access.class).size());// 16 "internal" attributes + 9 System.out
		assertEquals(0, TestVerveineUtils.selectElementsOfType(repo,LocalVariable.class).size());
	}

	@Test
	public void testClassProperties() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		assertEquals("Node", nodeClass.getName());
		assertEquals(11, nodeClass.getMethods().size());
		assertEquals(2, nodeClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo, Namespace.class, "lan"), nodeClass.getContainer());
		assertFalse(nodeClass.getIsInterface());

		fr.inria.verveine.core.gen.famix.Class interfce = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(interfce);
		assertEquals("IPrinter", interfce.getName());
		assertEquals(1, interfce.getMethods().size());
		assertEquals(0, interfce.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo, Namespace.class, "server"), interfce.getContainer());
		assertTrue(interfce.getIsInterface());

		fr.inria.verveine.core.gen.famix.Class innerClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(innerClass);
		assertEquals("XPrinter", innerClass.getName());
		assertEquals(2, innerClass.getMethods().size());
		assertEquals(1, innerClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer"), innerClass.getContainer());
		assertFalse(innerClass.getIsInterface());

		fr.inria.verveine.core.gen.famix.Class anonClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "anonymous(IPrinter)");
		assertNotNull(anonClass);
		assertEquals("anonymous(IPrinter)", anonClass.getName());
		assertEquals(1, anonClass.getMethods().size());
		assertEquals(0, anonClass.getAttributes().size());
		assertSame(TestVerveineUtils.detectElement(repo,Method.class, "PrintServer"), anonClass.getContainer());
		assertFalse(anonClass.getIsInterface());
	}

	@Test
	public void testNamedEntities() {
		JavaDictionary dico = new JavaDictionary(repo);
		
		assertNotSame(dico.ensureFamixClass(A_CLASS_NAME),dico.ensureFamixClass(A_CLASS_NAME));
		
		Namespace javaLang = dico.ensureFamixNamespaceJavaLang(null);
		assertEquals( JavaDictionary.OBJECT_PACKAGE_NAME, javaLang.getName());
		assertSame(javaLang, dico.ensureFamixNamespaceJavaLang(null));

		fr.inria.verveine.core.gen.famix.Class obj = dico.ensureFamixClassObject(null);
		assertEquals(JavaDictionary.OBJECT_NAME, obj.getName());
		assertSame(obj, dico.ensureFamixClassObject(null));
		assertEquals(0, obj.getSuperInheritances().size());
		assertSame(javaLang, obj.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class fmx = dico.ensureFamixClassStubOwner();
		assertEquals(JavaDictionary.STUB_METHOD_CONTAINER_NAME, fmx.getName());
		assertSame(fmx, dico.ensureFamixClassStubOwner());
	}

	@Test
	public void testInheritance() {
		fr.inria.verveine.core.gen.famix.Class clazz;
		Collection<Inheritance> superInheritances;
		Inheritance inh;
		
		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "PrintServer");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "OutputServer"), inh.getSuperclass());

		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		superInheritances = clazz.getSuperInheritances();
		assertEquals(1, superInheritances.size());
		inh = superInheritances.iterator().next();
		assertSame(clazz, inh.getSubclass());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME), inh.getSuperclass());
	}

	@Test
	public void testMethodProperties() {
		Method mweb = TestVerveineUtils.detectElement(repo,Method.class, "methodWithEmptyBody");
		assertNotNull(mweb);
		assertEquals("methodWithEmptyBody", mweb.getName());
		assertEquals("methodWithEmptyBody()", mweb.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node"), mweb.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,PrimitiveType.class, "void"), mweb.getDeclaredType());

		Method em = TestVerveineUtils.detectElement(repo,Method.class, "equalsMultiple");
		assertNotNull(em);
		assertEquals("equalsMultiple", em.getName());
		assertEquals("equalsMultiple(AbstractDestinationAddress)", em.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress"), em.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,PrimitiveType.class, "boolean"), em.getDeclaredType());

		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer");
		assertNotNull(clazz);
		Method n = null;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals("name")) {
				n = m;
				break;
			}
		}
		assertNotNull(n);
		assertEquals("name", n.getName());
		assertEquals("name()", n.getSignature());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "FileServer"), n.getParentType());
		assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), n.getDeclaredType());
	}

	@Test
	public void testFieldType() {
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		Collection<Attribute> l_atts = clazz.getAttributes();
		assertEquals(2, l_atts.size());
		for (Attribute a : l_atts) {
			if (a.getName().equals("nextNode")) {
				assertSame(clazz, a.getParentType());
				assertSame(clazz, a.getDeclaredType());
			}
			else if (a.getName().equals("name")) {
				assertSame(clazz, a.getParentType());
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), a.getDeclaredType());
			}
		}
	}

	@Test
	public void testStubs() {
		String javaLangName = JavaDictionary.OBJECT_PACKAGE_NAME.substring(JavaDictionary.OBJECT_PACKAGE_NAME.lastIndexOf('.')+1);
		Namespace ns = TestVerveineUtils.detectElement(repo,Namespace.class, javaLangName);
		assertNotNull(ns);
		assertEquals(5, ns.getTypes().size());  // Object,String,StringBuffer,AbstractStringBuilder,System
		assertTrue(ns.getIsStub());
			
		fr.inria.verveine.core.gen.famix.Class obj = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, JavaDictionary.OBJECT_NAME);
		assertNotNull(obj);
		assertTrue(ns.getIsStub());
		assertSame(ns, obj.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class str = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String");
		assertNotNull(str);
		assertTrue(str.getIsStub());
		assertSame(ns, str.getContainer());
		
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);
		assertFalse(clazz.getIsStub());
		
		/* [].length notused in the default test case 
		Attribute att = TestVerveineUtils.detectElement(repo, Attribute.class, "length");
		assertNotNull(att);
		clazz = (Class) att.getParentType();
		assertNotNull(clazz);
		assertEquals(JavaDictionary.ARRAYS_NAME, clazz.getName());*/
	}

	@Test
	public void testParameter() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		for (Method mNode : nodeClass.getMethods()) {
			if ( (mNode.getName().equals("Node")) ||
				 (mNode.getName().equals("methodWithEmptyBody")) ||
				 (mNode.getName().equals("canOriginate")) ||
				 (mNode.getName().equals("canOutput")) ) {
				assertEquals("Wrong number of parameter for method Node."+mNode.getName()+"()", 0, mNode.getParameters().size());
			}
			else if ( (mNode.getName().equals("name")) ||
					  (mNode.getName().equals("nextNode")) ) {
				assertTrue("Wrong number of parameter for method Node."+mNode.getName()+"()",  (mNode.getParameters().size()==0) || (mNode.getParameters().size()==1));
			}
			else if ( (mNode.getName().equals("accept")) ||
					 (mNode.getName().equals("send")) ||
					 (mNode.getName().equals("printOn")) ) {
				assertEquals("Wrong number of parameter for method Node."+mNode.getName()+"()", 1, mNode.getParameters().size());
			}
		}
		fr.inria.verveine.core.gen.famix.Class iprintClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "IPrinter");
		assertNotNull(iprintClass);
		Method mPrint = iprintClass.getMethods().iterator().next();
		assertEquals(2, mPrint.getParameters().size());
		for (Parameter p : mPrint.getParameters()) {
			assertSame(mPrint, p.getParentBehaviouralEntity());
			assertTrue(p.getName().equals("contents") || p.getName().equals("rv"));
			if (p.getName().equals("contents")) {
				assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), p.getDeclaredType());
			}
			if (p.getName().equals("rv")) {
				assertSame(TestVerveineUtils.detectElement(repo,PrimitiveType.class, "boolean"), p.getDeclaredType());
			}
		}
	}

	@Test
	public void testInvocation() {
		fr.inria.verveine.core.gen.famix.Class nodeClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(nodeClass);
		for (Method mNode : nodeClass.getMethods()) {
			if ( (mNode.getName().equals("name")) ||
				 (mNode.getName().equals("nextNode")) ||
				 (mNode.getName().equals("methodWithEmptyBody")) ||
				 (mNode.getName().equals("canOutput")) ||
				 (mNode.getName().equals("canOriginate")) ) {
				assertEquals("Wrong number of outgoing invocation for method Node."+mNode.getName()+"()", 0, mNode.getOutgoingInvocations().size());
			}
			else if ( (mNode.getName().equals("Node")) ||
					  (mNode.getName().equals("accept")) ) {
				assertEquals("Wrong number of outgoing invocation for method Node."+mNode.getName()+"()", 1, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("send"))  {
				assertEquals("Wrong number of outgoing invocation for method Node.send()", 6, mNode.getOutgoingInvocations().size());
			}
			else if (mNode.getName().equals("printOn")) {
				assertEquals("Wrong number of outgoing invocation for method Node.printOn()", 8, mNode.getOutgoingInvocations().size());
			}
		}
		
		fr.inria.verveine.core.gen.famix.Class sdaClass = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(sdaClass);
		for (Method mSDA : sdaClass.getMethods()) {
			for (Invocation inv : mSDA.getOutgoingInvocations()) {
				assertTrue( "Unexpected method signature: "+inv.getSignature(),
							inv.getSignature().equals("equalsSingle(String)") || inv.getSignature().equals("id()") || inv.getSignature().equals("equals(Object)"));
				if (inv.getSignature().equals("equalsSingle (String)")) {
					assertSame(sdaClass, ((Method)inv.getSender()).getParentType());
					assertEquals("self", inv.getReceiver().getName());
					assertEquals(1, inv.getCandidates().size());
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getCandidates().iterator().next());
				}
				else if (inv.getSignature().equals("id ()")) {
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals("self", inv.getReceiver().getName());
					assertEquals(1, inv.getCandidates().size());
					assertSame(sdaClass, ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
				else if (inv.getSignature().equals("equals (Object)")) {
					assertSame(TestVerveineUtils.detectElement(repo,Method.class, "equalsSingle"), inv.getSender());
					assertEquals(null, inv.getReceiver());
					assertEquals(1, inv.getCandidates().size());
					assertSame(TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "String"), ((Method)inv.getCandidates().iterator().next()).getParentType());
				}
			}
		}
	}
	
	@Test
	public void testAccess() {
		Attribute att;
		BehaviouralEntity accessor;
		
		att = TestVerveineUtils.detectElement(repo, Attribute.class, "name");
		assertNotNull(att);
		assertSame(2, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("name", accessor.getName());
		assertEquals("Node", ((Method)accessor).getParentType().getName());

		att = TestVerveineUtils.detectElement(repo, Attribute.class, "serverType");
		assertNotNull(att);
		assertSame(1, att.getIncomingAccesses().size());
		accessor = att.getIncomingAccesses().iterator().next().getAccessor();
		assertSame(Method.class, accessor.getClass());
		assertEquals("setServerType", accessor.getName());
		assertEquals("FileServer", ((Method)accessor).getParentType().getName());

		// finds method PrintServer.output()
		Method output = null;
		for (Method m : TestVerveineUtils.listElements(repo, Method.class, "output")) {
			if (m.getParentType().getName().equals("PrintServer")) {
				output = m;
				break;
			}
		}
		assertNotNull(output);
		assertEquals("Wrong number of outgoing access for method PrintServer.output()", 4, output.getAccesses().size());
		for (Access acc : output.getAccesses()) {
			assertTrue("Unexpected field accessed: "+acc.getVariable().getName(),
						acc.getVariable().getName().equals("out") || acc.getVariable().getName().equals("printer"));
			assertEquals(output, acc.getAccessor());
		}
	}

	@Test
	public void testSourceAnchors() {
		SourceAnchor anc = null;
		
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "XPrinter");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for class XPrinter", ((FileAnchor)anc).getFileName().endsWith("moose/lan/server/PrintServer.java"));
		assertEquals(17, ((FileAnchor)anc).getStartLine());
		assertEquals(31, ((FileAnchor)anc).getEndLine());

		clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "Node");
		assertNotNull(clazz);

		anc = clazz.getSourceAnchor();
		assertNotNull(anc);
		assertSame(clazz, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for class Node", ((FileAnchor)anc).getFileName().endsWith("moose/lan/Node.java"));
		assertEquals(6, ((FileAnchor)anc).getStartLine());
		assertEquals(70, ((FileAnchor)anc).getEndLine());
		
		Method meth = TestVerveineUtils.detectElement(repo, Method.class, "equalsMultiple");
		assertNotNull(meth);

		anc = meth.getSourceAnchor();
		assertNotNull(anc);
		assertSame(meth, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for method SingleDestinationAddress.equalsMultiple()", ((FileAnchor)anc).getFileName().endsWith("moose/lan/SingleDestinationAddress.java"));
		assertEquals(39, ((FileAnchor)anc).getStartLine());
		assertEquals(41, ((FileAnchor)anc).getEndLine());
		
		Attribute att = TestVerveineUtils.detectElement(repo, Attribute.class, "originator");
		assertNotNull(meth);

		anc = att.getSourceAnchor();
		assertNotNull(anc);
		assertSame(att, anc.getElement());
		assertSame(FileAnchor.class, anc.getClass());
		assertTrue("Wrong file source for field Packet.originator", ((FileAnchor)anc).getFileName().endsWith("moose/lan/Packet.java"));
		assertEquals(15, ((FileAnchor)anc).getStartLine());
		assertEquals(15, ((FileAnchor)anc).getEndLine());
		
	}

	@Test
	public void testModifiers() {
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo,fr.inria.verveine.core.gen.famix.Class.class, "OutputServer");
		assertNotNull(clazz);
		assertFalse(clazz.getIsInterface());
		assertTrue(clazz.getIsAbstract());
		assertTrue(clazz.getModifiers().contains("abstract"));
		assertTrue(clazz.getIsPublic());
		assertFalse(clazz.getIsPrivate());
		assertFalse(clazz.getIsProtected());
		assertFalse(clazz.getIsFinal());
		
		assertEquals(3, clazz.getMethods().size());
		for (Method m : clazz.getMethods()) {
			assertTrue(m.getIsPublic());
			assertFalse(m.getIsPrivate());
			assertFalse(m.getIsProtected());
			assertFalse(m.getIsFinal());
			if (m.getName().equals("output")) {
				assertTrue(m.getIsAbstract());
			}
			else {
				assertFalse(m.getIsAbstract());
			}
		}
		
		assertEquals(1, clazz.getAttributes().size());
		Attribute a = clazz.getAttributes().iterator().next();
		assertFalse(a.getIsPublic());
		assertFalse(a.getIsPrivate());
		assertTrue(a.getIsProtected());
		assertFalse(a.getIsFinal());
	}

	@Test
	public void testComment() {	
		fr.inria.verveine.core.gen.famix.Class clazz = TestVerveineUtils.detectElement(repo, fr.inria.verveine.core.gen.famix.Class.class, "SingleDestinationAddress");
		assertNotNull(clazz);
		Collection<Comment> cmts = clazz.getComments();
		assertEquals(1, cmts.size());
		SourceAnchor anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(6, ((FileAnchor)anc).getStartLine());
		assertEquals(10, ((FileAnchor)anc).getEndLine());

		Method meth = TestVerveineUtils.detectElement(repo, Method.class, "equalsSingle");
		assertNotNull(meth);
		cmts = meth.getComments();
		assertEquals(1, cmts.size());
		anc = cmts.iterator().next().getSourceAnchor();
		assertEquals(31, ((FileAnchor)anc).getStartLine());
		assertEquals(34, ((FileAnchor)anc).getEndLine());

	}
	
	@Test
	public void testMetric() {	
		for (Method m : TestVerveineUtils.listElements(repo, Method.class, "accept")) {
			assertNotNull(m);
			fr.inria.verveine.core.gen.famix.Class owner = (fr.inria.verveine.core.gen.famix.Class) m.getParentType();
			assertNotNull(owner);
			if (owner.getName().equals("OutputServer")) {
			assertEquals(2, m.getCyclo());
				assertEquals(6, m.getNOS());
			}
			else if (owner.getName().equals("Node")) {
				assertEquals(1, m.getCyclo());
				assertEquals(1, m.getNOS());
			}
			else if (owner.getName().equals("WorkStation")) {
				assertEquals(2, m.getCyclo());
				assertEquals(7, m.getNOS());
			}
		}		
	}

}