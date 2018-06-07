package controller;

import dao.UserAuthenticationQuery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;

public class SignUpController
{
    //inizializzo tutti i vari oggetti grafici
    @FXML
    private Label error;
    @FXML
    private TextField txtuser;
    @FXML
    private PasswordField txtpass;
    @FXML
    private PasswordField txtrpass;
    @FXML
    private TextField txtname;
    @FXML
    private TextField txtsurname;
    @FXML
    private TextField txtemail;
    @FXML
    private Button btsubmit;
    @FXML
    private Button btback;


    public void Submit(ActionEvent event) throws Exception
    {
        //Controllo che nei campi Username,password,email non venga inserito un testo vuoto
        if (!txtpass.getText().matches("") && !txtuser.getText().matches("") && !txtemail.getText().matches(""))
        {

            //controllo che il PasswordField txtpass sia uguale a quello del PasswordField txtrpass
            if (txtpass.getText().equals(txtrpass.getText()))
            {

                try {
                    UserAuthenticationQuery userDaoInterface = new UserAuthenticationQuery();
                    //faccio una chiamata al metodo SignInQuery1 passandogli le stringhe scritte nei campi di testo grafico e metto il risultato in una variabile di tipo integer
                   // int result = new UserAuthenticationQuery().SignInQuery(txtuser.getText(), txtpass.getText(),txtname.getText(),txtsurname.getText(), txtemail.getText());
                    int result= userDaoInterface.SignInQuery(txtuser.getText(),txtpass.getText(),txtname.getText(),txtsurname.getText(), txtemail.getText());
                    if (result == 1) {
                        Stage stage;
                        Parent home;

                        stage = (Stage) btsubmit.getScene().getWindow();
                        home = FXMLLoader.load(getClass().getResource("../view/Login.fxml"));

                        Scene homescene = new Scene(home);
                        stage.setScene(homescene);
                        stage.setTitle("JLibrary");

                        stage.show();
                    } else {
                        error.setText("Errore");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                error.setText("Error,The two PasswordField don't match, try again");
            }
        } else {
            if (txtuser.getText().equals("")) {
                error.setText("Error,Username empty,try again");
            } else {
                if (txtpass.getText().equals("")) {
                    error.setText("Error,Password empty,try again");
                } else {
                    error.setText("Error,Email empty,try again");
                }
            }
            {

            }
        }
    }

    public  void gotologin(javafx.event.ActionEvent event) throws IOException
    {
        Stage back;
        Parent gologin;

        back =(Stage) btback.getScene().getWindow();
        gologin = FXMLLoader.load(getClass().getResource("../view/Login.fxml"));
        back.setTitle("JLibrary");
        Scene homescene = new Scene(gologin);
        back.setScene(homescene);
        back.show();
    }
}