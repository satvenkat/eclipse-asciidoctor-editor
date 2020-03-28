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
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class AsciiDoctorImageCopyProvider extends AbstractAsciiDoctorProvider{
	private static ImageFilesFilter IMAGE_FILTER = new ImageFilesFilter();
	

	AsciiDoctorImageCopyProvider(AsciiDoctorProjectProviderContext context) {
		super(context);
	}
	
	private void copyImagesToOutputFolder(String sourcePath, File target) {
		getContext().getLogAdapter().resetTimeDiff();
		File cachedImagesFile = new File(sourcePath);
		if (!cachedImagesFile.exists()) {
			return;
		}
		try {
			FileUtils.copyDirectory(cachedImagesFile, target, IMAGE_FILTER);
		} catch (IOException e) {
			getContext().getLogAdapter().logError("Cannot copy images", e);
		}
		getContext().getLogAdapter().logTimeDiff("copied images to output folder:"+sourcePath);

	}

	public void ensureImages() {
		Path outputFolder = getContext().getOutputFolder();
		if (outputFolder==null){
			throw new IllegalStateException("no output folder set!");
		}
		File targetImagesDir = getContext().getTempFolder().toFile();
		copyImagesToOutputFolder(getContext().getRootDirectory().getAbsolutePath(), targetImagesDir);
		getContext().targetImagesDir=targetImagesDir;

	}
	
	private static class ImageFilesFilter implements FileFilter{

		@Override
		public boolean accept(File file) {
			if (file==null || ! file.exists()){
				return false;
			}
			if (file.isDirectory()){
				return true;
			}
			String ext = FilenameUtils.getExtension(file.getName());
			if (ext==null || ext.isEmpty()){
				return false;
			}
			String e = ext.toLowerCase();
			if ("png".equals(e)){
				return true;
			}
			if ("jpg".equals(e)){
				return true;
			}
			if ("gif".equals(e)){
				return true;
			}
			if ("svg".equals(e)){
				return true;
			}
			if ("bmp".equals(e)){
				return true;
			}
			if ("tiff".equals(e)){
				return true;
			}
			return false;
		}
		
	}

	public void reset() {
	}

}