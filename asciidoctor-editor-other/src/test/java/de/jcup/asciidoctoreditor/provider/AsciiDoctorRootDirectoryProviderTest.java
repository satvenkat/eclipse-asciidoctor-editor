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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.asciidoctoreditor.LogAdapter;

public class AsciiDoctorRootDirectoryProviderTest {

	private AsciiDoctorProjectProviderContext context;
	private AsciiDoctorRootDirectoryProvider providerToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
    private LogAdapter logAdapter; 
	
	@Before
	public void before(){
		context = mock(AsciiDoctorProjectProviderContext.class);
		logAdapter = mock(LogAdapter.class);
		when(context.getLogAdapter()).thenReturn(logAdapter);
		providerToTest = new AsciiDoctorRootDirectoryProvider(context);
	}
	
	@Test
	public void when_no_asciidocfile_is_set_an_illegal_state_exception_is_thrown() {
		/* test */
		expectedException.expect(IllegalStateException.class);
		
		/* execute */
		providerToTest.getRootDirectory();
	}
	
	@Test
    public void asciidoc_files_having_same_name_and_no_common_base_adoc_file_are_rendered_correctly() {
        
        File asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder1/001_article.adoc");
        File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder1/");

        /* prepare */
        when(context.getAsciiDocFile()).thenReturn(asciidocFile);
        
        /* execute */
        File baseDir = providerToTest.getRootDirectory();

        /* test */
        assertNotNull(baseDir);
        assertEquals(expectedbaseDir.toString(),baseDir.toString());
        
        /* ------ */
        /* phase 2*/       // we use now subfolder2 with same name
        /* ------ */
        
        asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/001_article.adoc");
        expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/");

        /* prepare */
        when(context.getAsciiDocFile()).thenReturn(asciidocFile);
        
        /* execute */
        baseDir = providerToTest.getRootDirectory();

        /* test */
        assertNotNull(baseDir);
        assertEquals(expectedbaseDir.toString(),baseDir.toString());
    }
    
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy1() {
		
		File asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/testproject1.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		File baseDir = providerToTest.getRootDirectory();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy2() {
		
		File asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/sub2/testproject2.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		File baseDir = providerToTest.getRootDirectory();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
	public void when_calculated_base_dir_user_local_temp_an_illegal_state_exception_is_thrown() throws Exception {
		
		File asciidocFile = Files.createTempFile("prefix", "suffix").toFile();
		/* sanity check - it must be clear the the parent directory is potentially problematic- e.g. on windows : C:\Users\$username\AppData\Local\Temp*/
		File problematic = new File(System.getProperty("java.io.tmpdir"));
		assertEquals(problematic, asciidocFile.getParentFile());

		/* test */
		expectedException.expect(IllegalStateException.class);
		
		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		providerToTest.getRootDirectory();

	}
	
	protected File ensuredTestFile(String pathname) {
		File asciidocFile = new File(pathname);
		if (! asciidocFile.exists()){
			asciidocFile=new File("asciidoctor-editor-other/"+pathname);
		}
		if (! asciidocFile.exists()){
			throw new IllegalStateException("test case corrupt-file not found:"+asciidocFile);
		}
		return asciidocFile;
	}

}