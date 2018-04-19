/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;

public abstract class ToolbarAction extends Action {

	protected final AsciiDoctorEditor asciiDoctorEditor;

	public ToolbarAction(AsciiDoctorEditor asciiDoctorEditor) {
		this.asciiDoctorEditor = asciiDoctorEditor;
	}
	
	static ImageDescriptor createToolbarImageDescriptor(String name) {
		return AsciiDoctorEditorUtil.createImageDescriptor("icons/toolbar/" + name);
	}
}
