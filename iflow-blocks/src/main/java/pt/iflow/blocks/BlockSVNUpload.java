package pt.iflow.blocks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.svn.SVNUtils;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.config.RepositorySVN;
import pt.iflow.connector.document.Document;

/**
 * <p>
 * Title: BlockValidacoes
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class BlockSVNUpload extends Block {
  public Port portIn, portSuccess, portError;

  private static final String sREPOSITORY = "repository";
  private static final String sFILE_VARIABLE = "fileVariable";
  public static final String sUSERNAME_FROM_BLOCK = "username";
  public static final String sPASSWORD_FROM_BLOCK = "password";
  public static final String sDIRPATH_FROM_BLOCK = "dirpath";
  public static final String sCOMMENTARY = "commentary";
  protected static final String sDontEncryptPassword = "DontEncryptPassword";

  public BlockSVNUpload(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    isCodeGenerator = false;
    hasInteraction = false;
    saveFlowState = false;
  }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  @Override
  public Port getEventPort() {
    return null;
  }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @return always 'true'
   */
  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  @Override
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portError;

    String login = userInfo.getUtilizador();

    try {

      String sRepository = null;
      String sFileVariable = null;
      String urlSvn = null;
      String userNameSvn = null;
      String userPasswordSvn = null;
      String dirPath = "";
      String sUserNameFromBlock = null;
      String sPasswordFromBlock = null;
      String sDirpathFromBlock = null;
      String sCommentary = null;
      boolean isPasswordEncripted = false;
      
      sUserNameFromBlock = (String) (procData.eval(userInfo, this.getAttribute(sUSERNAME_FROM_BLOCK)));
      sPasswordFromBlock = (String) (procData.eval(userInfo, this.getAttribute(sPASSWORD_FROM_BLOCK)));
      sDirpathFromBlock = (String) (procData.eval(userInfo, this.getAttribute(sDIRPATH_FROM_BLOCK)));
      sCommentary = (String) (procData.eval(userInfo, this.getAttribute(sCOMMENTARY)));
      sRepository = (String) (procData.eval(userInfo, this.getAttribute(sREPOSITORY)));
      sFileVariable = this.getAttribute(sFILE_VARIABLE);
      
      try {
        isPasswordEncripted = !Boolean.parseBoolean(this.getAttribute(sDontEncryptPassword));
      } catch (Exception e) {}

      if (StringUtils.isEmpty(sRepository) || StringUtils.isEmpty(sFileVariable)) {
        Logger.warning(login, this, "after", "Repository = [" + sRepository + "] FileVariable = [" + sFileVariable + "]");
      } else {
        ArrayList<RepositorySVN> repositoriesList = Const.getConfig().getRepositoriesList();
        if (StringUtils.isEmpty(sUserNameFromBlock) || StringUtils.isEmpty(sPasswordFromBlock)) {
          Logger.warning(login, this, "after", "sUserNameFromBlock = [" + sUserNameFromBlock + "] sPasswordFromBlock = ["
              + sPasswordFromBlock + "]");
          for (RepositorySVN repository : repositoriesList) {
            if (sRepository.equals(repository.getName())) {
              urlSvn = repository.getUrl();
              userNameSvn = repository.getUsername();
              userPasswordSvn = repository.getPassword();
            }
          }
        } else {
          for (RepositorySVN repository : repositoriesList) {
            if (sRepository.equals(repository.getName())) {
              urlSvn = repository.getUrl();
            }
            userNameSvn = sUserNameFromBlock;
            if (isPasswordEncripted)
              userPasswordSvn = Utils.decrypt(sPasswordFromBlock);
            else
              userPasswordSvn = sPasswordFromBlock;
          }
        }

        Documents docBean = BeanFactory.getDocumentsBean();
        ProcessListVariable docList = procData.getList(sFileVariable);
        Document[] document = new Document[docList.size()];
        List<byte[]> docContent = new ArrayList<byte[]>();
        for (int i = 0; i < docList.size(); i++) {
          document[i] = docBean.getDocument(userInfo, procData,
              new DocumentData(Integer.parseInt(docList.getItemValue(i).toString())));
          docContent.add(document[i].getContent());
        }
        
        if (StringUtils.isEmpty(sCommentary)){
          sCommentary = "File added";
        }
        if (StringUtils.isEmpty(sDirpathFromBlock)){
          dirPath = "";
        } else {
          dirPath = sDirpathFromBlock;
        }
        
        for (int i = 0; i < docContent.size(); i++) {

          SVNRepository repository = SVNUtils.getSvnRepository(urlSvn, userNameSvn, userPasswordSvn);

          ISVNEditor editor = repository.getCommitEditor(sCommentary, null);

          try {

            SVNCommitInfo commitInfo = SVNUtils.addDir(editor, dirPath, document[i].getFileName(), docContent.get(i));
            outPort = portSuccess;
            System.out.println("The directory was added: " + commitInfo);

          } catch (SVNException e) {

            if (e.getErrorMessage().getErrorCode() == SVNErrorCode.RA_DAV_ALREADY_EXISTS) {

              SVNProperties fileProperties = new SVNProperties();
              ByteArrayOutputStream baos = new ByteArrayOutputStream();

              repository = SVNUtils.getSvnRepository(urlSvn, userNameSvn, userPasswordSvn);
              repository.getFile(dirPath + "/" + document[i].getFileName(), -1, fileProperties, baos);
              byte[] oldData = baos.toByteArray();

              editor = repository.getCommitEditor(sCommentary, null);

              SVNCommitInfo commitInfo = SVNUtils
                  .modifyFile(editor, dirPath, document[i].getFileName(), oldData, docContent.get(i));
              System.out.println("The file was changed: " + commitInfo);
              outPort = portSuccess;
            }
          }
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);

    return outPort;
  }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Cópia");
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Cópia Efectuada");
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
