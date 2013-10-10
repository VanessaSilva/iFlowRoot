package pt.iflow.api.utils.config;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import pt.iflow.api.utils.config.Config;

public class ConfigXMLReader {

  public static Config readConfigFile(String xmlFilePath) {

    try {

      // create JAXB context and initializing Marshaller
      JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      // specify the location and name of xml file to be read
      File file = new File(xmlFilePath);

      // this will create Java object
      return(Config) jaxbUnmarshaller.unmarshal(file);

    } catch (JAXBException e) {
      // some exception occured
      e.printStackTrace();
    }
    return null;
  }
}
