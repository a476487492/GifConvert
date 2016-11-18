package test.com.getting.util;

import com.getting.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * FileUtil Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
public class FileUtilTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: formatFileSize(@NotNull File file)
     */
    @Test
    public void testFormatFileSize() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: ensureFileNameAvailable(@NotNull File file)
     */
    @Test
    public void testEnsureFileNameAvailable() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getFileNameWithoutExtension(@NotNull File file)
     */
    @Test
    public void testGetFileNameWithoutExtension() throws Exception {
        File file = new File("d:\\test.mp4");
        Assert.assertTrue("test".equals(FileUtil.getFileNameWithoutExtension(file)));
    }

    /**
     * Method: getFileNameExtension(@NotNull File file)
     */
    @Test
    public void testGetFileNameExtension() throws Exception {
        File file = new File("d:\\test.mp4");
        Assert.assertTrue(".mp4".equals(FileUtil.getFileNameExtension(file)));
    }

    /**
     * Method: openFileDirectory(@NotNull File file)
     */
    @Test
    public void testOpenFileDirectory() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: ensureDirectoryAvailable(@NotNull File outputDirectory)
     */
    @Test
    public void testEnsureDirectoryAvailable() throws Exception {
//TODO: Test goes here... 
    }


} 
