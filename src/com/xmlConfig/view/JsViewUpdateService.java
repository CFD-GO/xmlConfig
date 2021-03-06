package com.xmlConfig.view;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.xmlConfig.view.jsComponent.JsBoxComponent;
import com.xmlConfig.view.jsComponent.JsGeometryComponent;
import com.xmlConfig.view.jsComponent.JsWedgeComponent;

public class JsViewUpdateService {

	private Map<Integer, JsXmlComponent> components = new HashMap<>();
	private ThemeResource res;
	private BrowserFrame frame;
	private HorizontalSplitPanel horizontalSplit;
	private HorizontalLayout filePanel;
	
	public JsViewUpdateService(HorizontalSplitPanel horizontalSplit, HorizontalLayout filePanel){
		this.horizontalSplit = horizontalSplit;
		this.filePanel = filePanel;
		res = new ThemeResource("index.html");
		setFrame();
	}
	
	
	public void updateJsPanel(){
		setFrame();	
	}
	
	public void clear(){
		removeJsComponents();
		components.clear();
	}
	
	public void addComponent(Element element, int id){
		JsXmlComponent component;
		switch(element.getNodeName()){
			case "Geometry":
				component = new JsGeometryComponent();
				components.put(id, component);
				filePanel.addComponent((AbstractJavaScriptComponent)component);
				filePanel.addComponent((AbstractJavaScriptComponent)component);
				setComponentState(id, element, component);
				break;
			case "Box":
				component = new JsBoxComponent();
				components.put(id, component);
				filePanel.addComponent((AbstractJavaScriptComponent)component);
				setComponentState(id, element, component);
				break;
			case "Wedge":
				component = new JsWedgeComponent();
				components.put(id, component);
				filePanel.addComponent((AbstractJavaScriptComponent)component);
				setComponentState(id, element, component);
				break;
		}	
	}
	
	public void updateComponent(int id, String propertyName, String value){
		JsXmlComponent component = components.get(id);
		if(component != null)
			component.updateState(propertyName, convertDimension(value));	
	}
	
	private void removeJsComponents() {
		for(JsXmlComponent c: components.values())
			filePanel.removeComponent((AbstractJavaScriptComponent)c);		
	}
	
	private void setFrame(){
		frame = new BrowserFrame("Static", res);
        frame.setWidth("100%");
		frame.setHeight("100%");
        horizontalSplit.setSecondComponent(frame);
	}

	private String convertDimension(String value){
		if(value.contains("m"))
			return value.replace("m", "") + "00";
		
		return value;
	}
	
	private void setComponentState(int id, Element element, JsXmlComponent component){
		NamedNodeMap attr = element.getAttributes();
		for(int i = 0; i < attr.getLength(); i++)
			component.setState(id, attr.item(i).getNodeName(),
					           convertDimension(attr.item(i).getNodeValue()));	
	}	
}
