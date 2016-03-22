package com.sicpa.standard.sasscl.productionParameterSelection.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

public abstract class AbstractProductionParametersNode<T> implements IProductionParametersNode {

	private static final long serialVersionUID = 1L;
	final protected List<IProductionParametersNode> children = new ArrayList<IProductionParametersNode>();
	protected ImageIcon image;
	protected String text;
	protected T value;

	public AbstractProductionParametersNode() {

	}

	public List<IProductionParametersNode> getChildren() {
		return this.children;
	}

	public void addChildren(final Collection<IProductionParametersNode> children) {

		if (null == children)
			return;

		for (IProductionParametersNode comp : children) {
			this.children.add(comp);
		}
	}

	public void addChildren(final IProductionParametersNode... children) {

		for (IProductionParametersNode comp : children) {
			this.children.add(comp);
		}
	}

	public ImageIcon getImage() {
		return this.image;
	}

	public void setImage(final ImageIcon image) {
		this.image = image;
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(final T value) {
		this.value = value;
	}

	public boolean isLeaf() {
		return this.children == null || this.children.isEmpty();
	}

	@Override
	public String toString() {
		String res = text + "\n";
		if (children != null) {
			for (IProductionParametersNode node : children) {
				res += "\t" + node.toString();
			}
		}
		return res;
	}
}
