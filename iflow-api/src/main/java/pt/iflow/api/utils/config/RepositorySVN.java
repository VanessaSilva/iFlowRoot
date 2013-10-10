package pt.iflow.api.utils.config;

import javax.xml.bind.annotation.XmlRootElement;

//Below statement means that class "Country.java" is the root-element of our example
@XmlRootElement(namespace = "pt.iflow.api.utils.config.Config")
public class RepositorySVN {

  private String name;
  private String url;
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RepositorySVN [name=");
    builder.append(name);
    builder.append(", url=");
    builder.append(url);
    builder.append(", username=");
    builder.append(username);
    builder.append(", password=");
    builder.append(password);
    builder.append("]");
    return builder.toString();
  }
}
