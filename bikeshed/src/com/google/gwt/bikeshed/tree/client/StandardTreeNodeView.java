/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.bikeshed.tree.client;

import com.google.gwt.bikeshed.cells.client.Cell;
import com.google.gwt.bikeshed.tree.client.TreeViewModel.NodeInfo;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;

import java.util.List;

/**
 * A view of a tree node.
 *
 * @param <T> the type that this {@link TreeNodeView} contains
 */
public class StandardTreeNodeView<T> extends TreeNodeView<T> {

  /**
   * Construct a {@link TreeNodeView}.
   *
   * @param tree the parent {@link TreeView}
   * @param parent the parent {@link TreeNodeView}
   * @param parentNodeInfo the {@link NodeInfo} of the parent
   * @param elem the outer element of this {@link TreeNodeView}.
   * @param value the value of this node
   */
  StandardTreeNodeView(final TreeView tree,
      final StandardTreeNodeView<?> parent, NodeInfo<T> parentNodeInfo,
      Element elem, T value) {
    super(tree, parent, parentNodeInfo, value);
    setElement(elem);
  }

  @Override
  protected void postClose() {
    getTree().maybeAnimateTreeNode(this);
  }

  @Override
  protected <C> TreeNodeView<C> createTreeNodeView(NodeInfo<C> nodeInfo,
      Element childElem, C childValue, Void viewData, int idx) {
    return new StandardTreeNodeView<C>(getTree(), this, nodeInfo, childElem,
        childValue);
  }

  @Override
  protected <C> void emitHtml(StringBuilder sb, List<C> childValues,
      List<TreeNodeView<?>> savedViews, Cell<C, Void> cell) {
    TreeView tree = getTree();
    TreeViewModel model = tree.getTreeViewModel();
    int imageWidth = tree.getImageWidth();

    int idx = 0;
    for (C childValue : childValues) {
      sb.append("<div style=\"position:relative;padding-left:");
      sb.append(imageWidth);
      sb.append("px;\">");
      if (savedViews.get(idx) != null) {
        sb.append(tree.getOpenImageHtml(0));
      } else if (model.isLeaf(childValue, this)) {
        sb.append(LEAF_IMAGE);
      } else {
        sb.append(tree.getClosedImageHtml(0));
      }
      sb.append("<div>");
      cell.render(childValue, null, sb);
      sb.append("</div></div>");
    }
  }

  /**
   * Ensure that the child container exists and return it.
   *
   * @return the child container
   */
  @Override
  protected Element ensureChildContainer() {
    if (getChildContainer() == null) {
      // If this is a root node or the element does not exist, create it.
      Element animFrame = getElement().appendChild(
          Document.get().createDivElement());
      animFrame.getStyle().setPosition(Position.RELATIVE);
      animFrame.getStyle().setOverflow(Overflow.HIDDEN);
      animFrame.setId("animFrame");
      setChildContainer(animFrame.appendChild(Document.get().createDivElement()));
    }
    return getChildContainer();
  }

  /**
   * @return the element that contains the rendered cell
   */
  @Override
  protected Element getCellParent() {
    return getElement().getChild(1).cast();
  }

  /**
   * @return the image element
   */
  @Override
  protected Element getImageElement() {
    return getElement().getFirstChildElement();
  }
}
