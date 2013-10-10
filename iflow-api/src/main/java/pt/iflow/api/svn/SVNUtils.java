package pt.iflow.api.svn;

import java.io.ByteArrayInputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNUtils {

  public static SVNRepository getSvnRepository(String urlSvn, String userNameSvn, String userPasswordSvn) throws SVNException {
    /*
     * Initialize the library. It must be done before calling any method of the library.
     */
    DAVRepositoryFactory.setup();

    /*
     * URL that points to repository.
     */
    SVNURL url = SVNURL.parseURIEncoded(urlSvn);

    /*
     * Create an instance of SVNRepository class. This class is the main entry point for all "low-level" Subversion operations
     * supported by Subversion protocol.
     * 
     * These operations includes browsing, update and commit operations. See SVNRepository methods javadoc for more details.
     */
    SVNRepository repository = SVNRepositoryFactory.create(url);

    /*
     * User's authentication information (name/password) is provided via an ISVNAuthenticationManager instance. SVNWCUtil creates a
     * default authentication manager given user's name and password.
     * 
     * Default authentication manager first attempts to use provided user name and password and then falls back to the credentials
     * stored in the default Subversion credentials storage that is located in Subversion configuration area. If you'd like to use
     * provided user name and password only you may use BasicAuthenticationManager class instead of default authentication manager:
     * 
     * authManager = new BasicAuthenticationsManager(userName, userPassword);
     * 
     * You may also skip this point - anonymous access will be used.
     */
    ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userNameSvn, userPasswordSvn);
    repository.setAuthenticationManager(authManager);

    return repository;
  }

  // public static SVNCommitInfo addDir(ISVNEditor editor, String filePath, byte[] data)
  // throws SVNException {
  // }

  /*
   * This method performs commiting an addition of a directory containing a file.
   */
  public static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, byte[] data) throws SVNException {
    /*
     * Always called first. Opens the current root directory. It means all modifications will be applied to this directory until a
     * next entry (located inside the root) is opened/added.
     * 
     * -1 - revision is HEAD (actually, for a comit editor this number is irrelevant)
     */
    editor.openRoot(-1);

    /*
     * Adds a new directory (in this case - to the root directory for which the SVNRepository was created). Since this moment all
     * changes will be applied to this new directory.
     * 
     * dirPath is relative to the root directory.
     * 
     * copyFromPath (the 2nd parameter) is set to null and copyFromRevision (the 3rd) parameter is set to -1 since the directory is
     * not added with history (is not copied, in other words).
     */
    
    try {
      editor.addDir(dirPath, null, -1);
    } catch (SVNException e) {
      if (e.getErrorMessage().getErrorCode() == SVNErrorCode.RA_DAV_ALREADY_EXISTS) {
     // continue, the directory is open
      } else {
          throw e;
      }
    }

    /*
     * Adds a new file to the just added directory. The file path is also defined as relative to the root directory.
     * 
     * copyFromPath (the 2nd parameter) is set to null and copyFromRevision (the 3rd parameter) is set to -1 since the file is not
     * added with history.
     */
    editor.addFile(filePath, null, -1);

    /*
     * The next steps are directed to applying delta to the file (that is the full contents of the file in this case).
     */
    editor.applyTextDelta(filePath, null);

    /*
     * Use delta generator utility class to generate and send delta
     * 
     * Note that you may use only 'target' data to generate delta when there is no access to the 'base' (previous) version of the
     * file. However, using 'base' data will result in smaller network overhead.
     * 
     * SVNDeltaGenerator will call editor.textDeltaChunk(...) method for each generated "diff window" and then
     * editor.textDeltaEnd(...) in the end of delta transmission. Number of diff windows depends on the file size.
     */
    SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
    String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);

    /*
     * Closes the new added file.
     */
    editor.closeFile(filePath, checksum);

    /*
     * Closes the new added directory.
     */
    editor.closeDir();

    /*
     * Closes the root directory.
     */
    editor.closeDir();

    /*
     * This is the final point in all editor handling. Only now all that new information previously described with the editor's
     * methods is sent to the server for committing. As a result the server sends the new commit information.
     */
    return editor.closeEdit();
  }

  /*
   * This method performs committing file modifications.
   */
  public static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath, String filePath, byte[] oldData, byte[] newData)
      throws SVNException {
    /*
     * Always called first. Opens the current root directory. It means all modifications will be applied to this directory until a
     * next entry (located inside the root) is opened/added.
     * 
     * -1 - revision is HEAD
     */
    editor.openRoot(-1);

    /*
     * Opens a next subdirectory (in this example program it's the directory added in the last commit). Since this moment all
     * changes will be applied to this directory.
     * 
     * dirPath is relative to the root directory. -1 - revision is HEAD
     */
    editor.openDir(dirPath, -1);

    /*
     * Opens the file added in the previous commit.
     * 
     * filePath is also defined as a relative path to the root directory.
     */
    editor.openFile(filePath, -1);

    /*
     * The next steps are directed to applying and writing the file delta.
     */
    editor.applyTextDelta(filePath, null);

    /*
     * Use delta generator utility class to generate and send delta
     * 
     * Note that you may use only 'target' data to generate delta when there is no access to the 'base' (previous) version of the
     * file. However, here we've got 'base' data, what in case of larger files results in smaller network overhead.
     * 
     * SVNDeltaGenerator will call editor.textDeltaChunk(...) method for each generated "diff window" and then
     * editor.textDeltaEnd(...) in the end of delta transmission. Number of diff windows depends on the file size.
     */
    SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
    String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(oldData), 0, new ByteArrayInputStream(newData),
        editor, true);

    /*
     * Closes the file.
     */
    editor.closeFile(filePath, checksum);

    /*
     * Closes the directory.
     */
    editor.closeDir();

    /*
     * Closes the root directory.
     */
    editor.closeDir();

    /*
     * This is the final point in all editor handling. Only now all that new information previously described with the editor's
     * methods is sent to the server for committing. As a result the server sends the new commit information.
     */
    return editor.closeEdit();
  }

  /*
   * This method performs committing a deletion of a directory.
   */
  public static SVNCommitInfo deleteDir(ISVNEditor editor, String dirPath) throws SVNException {
    /*
     * Always called first. Opens the current root directory. It means all modifications will be applied to this directory until a
     * next entry (located inside the root) is opened/added.
     * 
     * -1 - revision is HEAD
     */
    editor.openRoot(-1);

    /*
     * Deletes the subdirectory with all its contents.
     * 
     * dirPath is relative to the root directory.
     */
    editor.deleteEntry(dirPath, -1);

    /*
     * Closes the root directory.
     */
    editor.closeDir();

    /*
     * This is the final point in all editor handling. Only now all that new information previously described with the editor's
     * methods is sent to the server for committing. As a result the server sends the new commit information.
     */
    return editor.closeEdit();
  }

  /*
   * This method performs how a directory in the repository can be copied to branch.
   */
  public static SVNCommitInfo copyDir(ISVNEditor editor, String srcDirPath, String dstDirPath, long revision) throws SVNException {
    /*
     * Always called first. Opens the current root directory. It means all modifications will be applied to this directory until a
     * next entry (located inside the root) is opened/added.
     * 
     * -1 - revision is HEAD
     */
    editor.openRoot(-1);

    /*
     * Adds a new directory that is a copy of the existing one.
     * 
     * srcDirPath - the source directory path (relative to the root directory).
     * 
     * dstDirPath - the destination directory path where the source will be copied to (relative to the root directory).
     * 
     * revision - the number of the source directory revision.
     */
    editor.addDir(dstDirPath, srcDirPath, revision);

    /*
     * Closes the just added copy of the directory.
     */
    editor.closeDir();

    /*
     * Closes the root directory.
     */
    editor.closeDir();

    /*
     * This is the final point in all editor handling. Only now all that new information previously described with the editor's
     * methods is sent to the server for committing. As a result the server sends the new commit information.
     */
    return editor.closeEdit();
  }

}
