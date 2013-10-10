package pt.iflow.api.utils.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

//Below annotation defines root element of XML file
@XmlRootElement(name = "Config")
public class Config {

  private ArrayList<RepositorySVN> repositoriesList;

  public ArrayList<RepositorySVN> getRepositoriesList() {
    return repositoriesList;
  }

  // XmLElementWrapper generates a wrapper element around XML representation
  @XmlElementWrapper(name = "RepositoriesSVN")
  // XmlElement sets the name of the entities in collection
  @XmlElement(name = "RepositorySVN")
  public void setRepositoriesList(ArrayList<RepositorySVN> repositoriesList) {
    this.repositoriesList = repositoriesList;
  }
}