/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.asciidoctoreditor.SimpleStringUtils;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class AsciiDoctorEditorTreeContentProvider implements ITreeContentProvider {

	private static final String ASCIIDOCTOR_SCRIPT_CONTAINS_ERRORS = "AsciiDoctorAccess script contains errors.";
	private static final String ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS = "AsciiDoctorAccess script does not contain any headlines";
	private static final Object[] RESULT_WHEN_EMPTY = new Object[] { ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS };
	private Object[] items;
	private Object monitor = new Object();

	AsciiDoctorEditorTreeContentProvider() {
		items = RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		synchronized (monitor) {
			if (inputElement != null && !(inputElement instanceof AsciiDoctorScriptModel)) {
				return new Object[] { "Unsupported input element:" + inputElement };
			}
			if (items != null && items.length > 0) {
				return items;
			}
		}
		return RESULT_WHEN_EMPTY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Item){
			Item item = (Item) parentElement;
			return item.getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Item){
			Item item = (Item) element;
			return item.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Item){
			Item item = (Item) element;
			return ! item.getChildren().isEmpty();
		}
		return false;
	}

	private Item[] createItems(AsciiDoctorScriptModel model) {
		Map<Integer, Item> parents = new HashMap<Integer, Item>();
		List<Item> list = new ArrayList<>();
		for (AsciiDoctorHeadline headline : model.getHeadlines()) {
			Item item = new Item();
			item.name = headline.getName();
			int deep = headline.getDeep();
			item.prefix = "H"+deep+" :";
			item.type = ItemType.HEADLINE;
			item.offset = headline.getPosition();
			item.length = headline.getLengthToNameEnd();
			item.endOffset = headline.getEnd();
			/* register as next parent at this deep */
			parents.put(deep, item);
			/* when not root deep, try to fetch parent */
			if (deep>1){
				Item parent = parents.get(deep-1);
				if (parent==null){
					list.add(item);
					continue;
				}else{
					parent.getChildren().add(item);
					item.parent=parent;
					/* no add to list */
				}
			}else{
				list.add(item);
			}
		}
		if (list.isEmpty()) {
			Item item = new Item();
			item.name = ASCIIDOCTOR_SCRIPT_DOES_NOT_CONTAIN_ANY_FUNCTIONS;
			item.type = ItemType.META_INFO;
			item.offset = 0;
			item.length = 0;
			item.endOffset = 0;
			list.add(item);
		}
		/* debug part */
		if (model.hasDebugTokens()) {
			if (model.hasErrors()) {
				Item item = new Item();
				item.name = ASCIIDOCTOR_SCRIPT_CONTAINS_ERRORS;
				item.type = ItemType.META_ERROR;
				item.offset = 0;
				item.length = 0;
				item.endOffset = 0;
				list.add(0, item);
			}
			for (ParseToken token : model.getDebugTokens()) {
				Item item = new Item();
				item.name = SimpleStringUtils.shortString(token.getText(), 40) + " :<- "
						+ token.createTypeDescription();
				item.type = ItemType.META_DEBUG;
				item.offset = token.getStart();
				item.length = token.getText().length();
				item.endOffset = token.getEnd();
				list.add(item);
			}
		}
		return list.toArray(new Item[list.size()]);

	}

	public void rebuildTree(AsciiDoctorScriptModel model) {
		synchronized (monitor) {
			if (model == null) {
				items = null;
				return;
			}
			items = createItems(model);
		}
	}

	public Item tryToFindByOffset(int offset) {
		synchronized (monitor) {
			if (items == null) {
				return null;
			}
			for (Object oitem : items) {
				if (!(oitem instanceof Item)) {
					continue;
				}
				Item item = (Item) oitem;
				int itemStart = item.getOffset();
				int itemEnd = item.getEndOffset();// old:
													// itemStart+item.getLength();
				if (offset >= itemStart && offset <= itemEnd) {
					return item;
				}
			}

		}
		return null;
	}

}