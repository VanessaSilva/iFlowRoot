package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributos
 *
 *  desc: dialogo para alterar atributos de um bloco
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import pt.iflow.api.db.DBTableHelper;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;

public class AlteraAtributosSVNUpload extends AlteraAtributos {
  private static final long serialVersionUID = 7353724963450325257L;

  protected static final String sRepository = "repository";
  protected static final String sRepositoryLabel = "Repository";
  protected static final String sFileVariable = "fileVariable";
  protected static final String sFileVariableLabel = "File Variable";
  protected static final String sUserNameLabel = "Username";
  protected static final String sPasswordLabel = "Password";
  protected static final String sDirpathLabel = "Dirpath";
  protected static final String sCommentaryLabel = "Commentary";
  protected static final String sDontEncryptPasswordLabel = "Não Encriptar Password";
  protected static final String sUserName = "username";
  protected static final String sPassword = "password";
  protected static final String sDirpath = "dirpath";
  protected static final String sCommentary = "commentary";
  protected static final String sDontEncryptPassword = "DontEncryptPassword";
  private JComboBox jcbRepositories;
  private JComboBox jcbFileVariables;
  private JTextField jtfUserNameFromBlock;
  private JTextField jtfPasswordFromBlock;
  private JTextField jtfDirpath;
  private JTextArea jtaCommentary;
  private JCheckBox jcbEncriptado;
  JComponent jValue = null;

  public AlteraAtributosSVNUpload(FlowEditorAdapter adapter) {
    super(adapter);
    initControls();
  }

  private static String[] fixNames(String[] array) {
    if (null == array) {
      return new String[] {};
    }
    for (int i = 0; i < array.length; i++) {
      array[i] = "\"" + array[i] + "\"";
    }
    return array;
  }

  void jbInit() {
    // Size
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });
    this.setModal(true);

    panel1.setLayout(borderLayout1);

    // Botão OK 
    okButton.setText(OK);
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });

    // Botão cancelar
    cancelButton.setText(Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });

    // Grids
    GridBagLayout sGridbag = new GridBagLayout();
    GridBagConstraints sC = new GridBagConstraints();
    sC.fill = GridBagConstraints.HORIZONTAL;

    // Panel
    JPanel jPanel = new JPanel();
    jPanel.setLayout(sGridbag);

    // Repository
    // label
    JLabel dsLabel = new JLabel(sRepositoryLabel + ": ");
    dsLabel.setHorizontalAlignment(JLabel.LEFT);
    dsLabel.setLabelFor(this.jcbRepositories);

    // separator
    JPanel sizer = new JPanel();
    sizer.setSize(5, 1);
    sGridbag.setConstraints(sizer, sC);
    jPanel.add(sizer);
    sGridbag.setConstraints(dsLabel, sC);
    jPanel.add(dsLabel);

    // combobox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jcbRepositories, sC);
    jPanel.add(jcbRepositories);
    sC.gridwidth = 1;

    // File Variable
    // label
    JLabel dsLabel2 = new JLabel(sFileVariableLabel + ": ");
    dsLabel2.setHorizontalAlignment(JLabel.LEFT);
    dsLabel2.setLabelFor(jcbFileVariables);

    // separator
    JPanel sizer2 = new JPanel();
    sizer2.setSize(5, 1);
    sGridbag.setConstraints(sizer2, sC);
    jPanel.add(sizer2);
    sGridbag.setConstraints(dsLabel2, sC);
    jPanel.add(dsLabel2);

    // combobox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jcbFileVariables, sC);
    jPanel.add(jcbFileVariables);
    sC.gridwidth = 1;

    /* table */
    JPanel jtmp = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    jtmp.setLayout(gridbag);

    // Nome
    // label
    JLabel dsLabel3 = new JLabel(sUserNameLabel + ": ");
    dsLabel3.setHorizontalAlignment(JLabel.LEFT);
    dsLabel3.setLabelFor(jtfUserNameFromBlock);

    // separator
    JPanel sizer3 = new JPanel();
    sizer3.setSize(5, 1);
    sGridbag.setConstraints(sizer3, sC);
    jPanel.add(sizer3);
    sGridbag.setConstraints(dsLabel3, sC);
    jPanel.add(dsLabel3);

    // Textbox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jtfUserNameFromBlock, sC);
    jPanel.add(jtfUserNameFromBlock);
    sC.gridwidth = 1;

    // Password
    // label
    JLabel dsLabel4 = new JLabel(sPasswordLabel + ": ");
    dsLabel4.setHorizontalAlignment(JLabel.LEFT);
    dsLabel4.setLabelFor(jtfPasswordFromBlock);

    // separator
    JPanel sizer4 = new JPanel();
    sizer4.setSize(5, 1);
    sGridbag.setConstraints(sizer4, sC);
    jPanel.add(sizer4);
    sGridbag.setConstraints(dsLabel4, sC);
    jPanel.add(dsLabel4);

    // Textbox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jtfPasswordFromBlock, sC);
    jPanel.add(jtfPasswordFromBlock);
    sC.gridwidth = 1;
    
    // Encriptado
    // label
    JLabel dsLabel7 = new JLabel(sDontEncryptPasswordLabel +  " ");
    dsLabel7.setHorizontalAlignment(JLabel.LEFT);
    dsLabel7.setLabelFor(jcbEncriptado);

    // separator
    JPanel sizer7 = new JPanel();
    sizer7.setSize(10, 1);
    sGridbag.setConstraints(sizer7, sC);
    jPanel.add(sizer7);
    sGridbag.setConstraints(dsLabel7, sC);
    jPanel.add(dsLabel7);

    // Checkbox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jcbEncriptado, sC);
    jPanel.add(jcbEncriptado);
    sC.gridwidth = 1;
    
    // Dirpath
    // label
    JLabel dsLabel5 = new JLabel(sDirpathLabel + ": ");
    dsLabel5.setHorizontalAlignment(JLabel.LEFT);
    dsLabel5.setLabelFor(jtfDirpath);

    // separator
    JPanel sizer5 = new JPanel();
    sizer5.setSize(5, 1);
    sGridbag.setConstraints(sizer5, sC);
    jPanel.add(sizer5);
    sGridbag.setConstraints(dsLabel5, sC);
    jPanel.add(dsLabel5);

    // Textbox
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jtfDirpath, sC);
    jPanel.add(jtfDirpath);
    sC.gridwidth = 1;

    // Commentary
    // label
    JLabel dsLabel6 = new JLabel(sCommentaryLabel + ": ");
    dsLabel6.setHorizontalAlignment(JLabel.LEFT);
    dsLabel6.setLabelFor(jtaCommentary);

    // separator
    JPanel sizer6 = new JPanel();
    sizer6.setSize(5, 1);
    sGridbag.setConstraints(sizer6, sC);
    jPanel.add(sizer6);
    sGridbag.setConstraints(dsLabel6, sC);
    jPanel.add(dsLabel6);

    // TextArea
    sC.gridwidth = GridBagConstraints.REMAINDER;
    sGridbag.setConstraints(jtaCommentary, sC);
    jPanel.add(jtaCommentary);
    sC.gridwidth = 1;

    // DESCRIPTION, RESULT
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(jPanel, c);
    jtmp.add(jPanel);

    sizer = new JPanel();
    sizer.setSize(1, 10);
    gridbag.setConstraints(sizer, c);
    jtmp.add(sizer);

    JPanel fullPanel = new JPanel();
    fullPanel.add(jtmp, BorderLayout.NORTH);
    fullPanel.setSize(10, 50);

    /* paineis */
    JPanel aux1 = new JPanel();
    aux1.setSize(50, 1);
    getContentPane().add(aux1, BorderLayout.WEST);
    JPanel aux2 = new JPanel();
    aux2.setSize(10, 1);
    getContentPane().add(aux2, BorderLayout.EAST);
    jScrollPane1.getViewport().add(fullPanel, null);
    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(okButton, null);
    jPanel2.add(cancelButton, null);
    this.getContentPane().add(jPanel3, BorderLayout.NORTH);

    dialogComponentResized(null);
    repaint();
  }

  private String[] getDocumentsFromCatalogue() {
    Object[] catalogue = adapter.getDesenho().getCatalogue().toArray();
    List<String> list = new ArrayList<String>();

    for (int i = 0; i < catalogue.length; i++) {
      if (catalogue[i] != null && ((Atributo) catalogue[i]).getDataType().equals("Document")) {
        list.add(((Atributo) catalogue[i]).getNome());
      }
    }

    String[] ret = new String[list.size()];
    return list.toArray(ret);
  }

  public void setDataIn(String title, List<Atributo> atributos) {
    atributos = parseAtributos(atributos);
    List<Atributo> myAttrs = new ArrayList<Atributo>();
    for (Atributo a : atributos) {
      myAttrs.add(a.cloneAtributo());
    }

    if (myAttrs != null) {
      for (Atributo a : myAttrs) {
        if (a == null)
          continue;
      }
    }
    super.setDataIn(title, myAttrs, 600, 400);
  }

  public String[][] getNewAttributes() {

    String[][] atributos = super.getNewAttributes();
    List<String[]> extra = this.getExtra();

    String[][] newAtributos = new String[atributos.length + extra.size()][];
    System.arraycopy(atributos, 0, newAtributos, 0, atributos.length);

    for (int i = 0; i < extra.size(); i++) {
      String[] item = extra.get(i);
      newAtributos[atributos.length + i] = item;
    }

    return newAtributos;
  }

  private List<String[]> getExtra() {
    List<String[]> retObj = new ArrayList<String[]>();

    String[] repositories = new String[2];
    repositories[0] = sRepository;
    repositories[1] = (String) jcbRepositories.getSelectedItem();
    retObj.add(repositories);

    String[] fileVariables = new String[2];
    fileVariables[0] = sFileVariable;
    fileVariables[1] = (String) jcbFileVariables.getSelectedItem();
    retObj.add(fileVariables);

    String[] userName = new String[2];
    userName[0] = sUserName;
    userName[1] = (String) jtfUserNameFromBlock.getText();
    retObj.add(userName);

    String[] password = new String[2];
    password[0] = sPassword;
    password[1] = (String) jtfPasswordFromBlock.getText();
    retObj.add(password);

    String[] encriptado = new String[2];
    encriptado[0] = sDontEncryptPassword;
    encriptado[1] = jcbEncriptado.isSelected()?"true":"false";
    retObj.add(encriptado);

    String[] dirpath = new String[2];
    dirpath[0] = sDirpath;
    dirpath[1] = (String) jtfDirpath.getText();
    retObj.add(dirpath);

    String[] commentary = new String[2];
    commentary[0] = sCommentary;
    commentary[1] = (String) jtaCommentary.getText();
    retObj.add(commentary);

    return retObj;
  }

  private List<Atributo> parseAtributos(List<Atributo> atributos) {
    List<Atributo> retObj = new ArrayList<Atributo>();
    DBTableHelper.clearCache();
    for (Atributo at : atributos) {
      if (StringUtils.equalsIgnoreCase(sRepository, at.getNome())) {
        String valor = at.getValor();
        boolean found = false;
        for (int i = 0; i < jcbRepositories.getItemCount(); i++) {
          String item = (String) jcbRepositories.getItemAt(i);
          if (StringUtils.equals(valor, item)) {
            jcbRepositories.setSelectedIndex(i);
            found = true;
            break;
          }
        }
        if (!found) {
          jcbRepositories.setModel(new DefaultComboBoxModel(prepareRepositoryComboBox(valor)));
        }
      } else if (StringUtils.equalsIgnoreCase(sFileVariable, at.getNome())) {
        String valor = at.getValor();
        for (int i = 0; i < jcbFileVariables.getItemCount(); i++) {
          String item = (String) jcbFileVariables.getItemAt(i);
          if (StringUtils.equals(valor, item)) {
            jcbFileVariables.setSelectedIndex(i);
            break;
          }
        }
      } else if (StringUtils.equalsIgnoreCase(sUserName, at.getNome())) {
        jtfUserNameFromBlock.setText(at.getValor());
      } else if (StringUtils.equalsIgnoreCase(sPassword, at.getNome())) {
        jtfPasswordFromBlock.setText(at.getValor());
      } else if (StringUtils.equalsIgnoreCase(sDirpath, at.getNome())) {
        jtfDirpath.setText(at.getValor());
      } else if (StringUtils.equalsIgnoreCase(sCommentary, at.getNome())) {
        jtaCommentary.setText(at.getValor());
      } else if (StringUtils.equalsIgnoreCase(sDontEncryptPassword, at.getNome())) {
        jcbEncriptado.setSelected(Boolean.valueOf(at.getValor()).booleanValue());
      } else {
        retObj.add(at);
      }
    }
    return retObj;
  }

  private Object[] prepareRepositoryComboBox(String defVal) {
    Object[] newValues = null;
    String[] values = getRepositories();
    if (values != null) {
      newValues = new Object[values.length + 1];
      System.arraycopy(values, 0, newValues, 1, values.length);
      newValues[0] = ((StringUtils.isBlank(defVal)) ? "" : defVal);
    }
    return newValues;
  }

  private String[] getRepositories() {
    String[] retObj;
    try {
      retObj = fixNames(adapter.getRepository().listRepositories());
    } catch (NullPointerException e) {
      retObj = new String[] {};
      adapter.log("Null Repositories");
    }
    return retObj;
  }

  private void initControls() { 
    jcbEncriptado = new JCheckBox();
    jtfUserNameFromBlock = new JTextField(25);
    jtfPasswordFromBlock = new JTextField(25);
    jtfDirpath = new JTextField(25);
    jtaCommentary = new JTextArea(5, 25);
    jtaCommentary.setPreferredSize(new Dimension(80,20));
    if (jcbRepositories == null) {
      jcbRepositories = new JComboBox(prepareRepositoryComboBox(null));
      jcbRepositories.setEditable(true);
      jcbRepositories.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          int idx = jcbRepositories.getSelectedIndex();
          if (idx < 0) { // user typed this entry
            Object obj = jcbRepositories.getSelectedItem();
            final int pos = 0;
            jcbRepositories.removeItemAt(pos);
            jcbRepositories.insertItemAt(obj, pos);
            jcbRepositories.setSelectedIndex(pos);
          }
        }
      });
    }
    if (jcbFileVariables == null) {
      jcbFileVariables = new JComboBox();
      Object[] elements = getDocumentsFromCatalogue();
      AutoCompleteSupport.install(jcbFileVariables, GlazedLists.eventListOf(elements));
    }
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
  }
}
