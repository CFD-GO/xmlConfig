package com.xmlConfig.view;



import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.xmlConfig.controller.XmlController;

@SuppressWarnings("serial")
@Theme("firstvaad")
public class XmlMainView extends UI implements XmlView { 


	private XmlController controller = new XmlController(this);
	private TreeTable tree;
	private VerticalLayout layout;
	private HorizontalLayout filePanel;
	private Button saveButton;
	private Button loadButton;
	private ComboBox fileList;
	private int nodeCounter = 0;
	private final String propertyName = "Property Name";
	private final String valueName = "Value";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = XmlMainView.class)
	public static class Servlet extends VaadinServlet {
	}
 
	@Override
	protected void init(VaadinRequest request) {	
		initComponents();
		addComponents();
		setButtonListeners();	
	}
	
	private void setButtonListeners(){
		loadButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String fileName = (String)fileList.getValue();
				controller.getFile(fileName);  	
			}
		});
		
		saveButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				controller.saveFile();		
			}
		});
			
	}
	
	private void setTreeListener(){
			tree.addItemClickListener(new ItemClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
            public void itemClick(ItemClickEvent event) {	
                final Integer itemId = (Integer) event.getItemId();
                final Integer parentItemId = (Integer) tree.getParent(itemId);                               
                final String propertyName = (String) event.getPropertyId();
                final Property<Component> containerProperty = tree.getContainerProperty(itemId, propertyName);
                Component property = containerProperty.getValue();
                    if (property instanceof Label) {
                    	
                    	final TextField field = new TextField();
                        Label labelProperty = (Label)property;                   
                        field.setValue(labelProperty.getValue());           
                        field.addBlurListener(new BlurListener() {
					    
                        	@Override
                            public void blur(BlurEvent event) {
                                containerProperty.setValue(new Label(field.getValue()));
                                if(itemId == 0)
                                	controller.updateFile(propertyName, itemId, 0, field.getValue() );
                                else
                                	controller.updateFile(propertyName, itemId, parentItemId, field.getValue() );
                            }
						});
                        containerProperty.setValue(field);
                        field.focus();  
                    }
            }
		});
		
	}
	
	private void addComponents(){
		filePanel.addComponent(fileList);
	    filePanel.addComponent(loadButton);
	    filePanel.addComponent(saveButton); 
		layout.addComponent(filePanel);
		
	}
	
	private void initComponents(){
		layout = new VerticalLayout();
		filePanel = new HorizontalLayout();
		saveButton  = new Button("SAVE");
		loadButton  = new Button("LOAD");
		fileList = new ComboBox();
		fileList.setInputPrompt("SELECT FILE");
		fileList.addItems(controller.getFileList());
		layout.setMargin(true);
		setContent(layout);  
		
	}
	private void initTree(){
		if(tree != null)
			layout.removeComponent(tree);
		tree = new TreeTable("XML Configuration");
		tree.addContainerProperty(propertyName, Component.class, null);
		tree.addContainerProperty(valueName, Component.class, null);
		tree.setWidth("400");
		tree.setEditable(true);
		tree.setImmediate(true);
		setTreeListener();
		layout.addComponent(tree);
		
	}
	
	private void addChildrenToTree(NodeList children, int id) {
	    if (children.getLength() > 0) {	
	    	int parentId = id;
	        for (int i = 0; i < children.getLength(); i++) {
	        	Node node = children.item(i);
	        	if(node instanceof Text)
		            continue;
	        	int childId = ++nodeCounter;	            	   
	            String childName = node.getNodeName();
	            tree.addItem(new Label[]{new Label(childName), new Label("")}, childId);
	            tree.setParent(childId, parentId);
	            addAttributesToTree(node, childId);
	            addChildrenToTree(node.getChildNodes(), childId);
	        }
	    }
	}
	
	
	private  void addAttributesToTree(Node node, int parentId){
		 if(node.hasAttributes()){
	        	NamedNodeMap attr = node.getAttributes();
	        	for(int j = 0; j < attr.getLength(); j++){
	        		int childId = ++nodeCounter;
	        		Attr attribute = (Attr) attr.item(j);
	        		tree.addItem(new Label[]{new Label(attribute.getName()),new Label(attribute.getValue())}, childId);
	        		tree.setParent(childId, parentId);
	        		tree.setChildrenAllowed(childId, false);
	        	}
	        }
	}

	public void displayFile(Document doc){
	    initTree();
	    Element root = doc.getDocumentElement();
	    String rootItem = root.getNodeName();
        int parentId = nodeCounter;
        
        tree.addItem(new Label[]{new Label(rootItem), new Label("")}, parentId);
        addAttributesToTree(root, parentId);	   
        addChildrenToTree(root.getChildNodes(), parentId);
	}


}