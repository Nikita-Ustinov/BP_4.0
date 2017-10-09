package email.client;



//import com.sun.xml.internal.ws.api.ha.StickyFeature;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailClient {

   
//    public static void main(String[] args) {
////       EmailClient em = new EmailClient("ustinov_nikita_01@mail.ru", "", "Just a test"); // Нужно создать экземпляр класса 
//                                                                                         //  от трех параметров
//                                                                                         //   email, Тема и само сообщение        
//    }
    
   private String user = "ustinov.nikita.01@gmail.com";
   private String pass =  "4981273nN"; 
   public static void sendMessege(String to, String sub, String msg) {
       EmailClient report = new EmailClient(to, sub, msg);
   }
   
   public EmailClient(String to, String sub, String msg) {
       Properties prop  = new Properties();
       prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
       prop.put("mail.smtp.auth", true);
       prop.put("mail.smtp.starttls.enable", true);
       prop.put("mail.smtp.host", "smtp.gmail.com");
       prop.put("mail.smtp.port", "587");
       
       Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
              return new javax.mail.PasswordAuthentication(user, pass);
            }
        });
       
      try {
          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress("no-reply"));
          message.setRecipients((Message.RecipientType.TO), InternetAddress.parse(to));
          message.setSubject(sub);
          message.setText(msg);
          
          Transport.send(message);
          
          System.out.println("Mail sent");
      }
      catch(Exception e) {
          System.out.println(e);
      }
   }
    
}
