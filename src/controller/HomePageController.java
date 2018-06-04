ppackage controller;

import com.jfoenix.controls.JFXButton;
import dao.SearchOperaQuery;
import dao.UserInfoQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import vo.OperaMetadati;
import vo.UserInfo;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HomePageController implements Initializable
{
    @FXML
    private JFXButton btlogout;
    @FXML
    private TextField txtsearch;
    @FXML
    private ChoiceBox<String> cbfilter;
    @FXML
    private Button btsearch;
    @FXML
    private TableView<OperaMetadati> tablesearch;
    @FXML
    private TableColumn<OperaMetadati,String> col_titolo;
    @FXML
    private TableColumn<OperaMetadati,String> col_autore;
    @FXML
    private TableColumn<OperaMetadati,String> col_genere;
    @FXML
    private TableColumn<OperaMetadati,String> col_link;

    private ObservableList<OperaMetadati> oblist;

    public String autore;
    public String titolo;
    public String genere;

    OperaMetadati operaMetadati= new OperaMetadati(titolo,autore,genere);

    public HomePageController() throws IOException
    {
    }

    public static void setscene(ActionEvent event)
    {
        Parent root;
        try
        {
            root = FXMLLoader.load(HomePageController.class.getResource("../view/home.fxml"));
            Stage stage = new Stage();
            stage.setTitle("HomePage");
            Scene home = new Scene(root);
            stage.setScene(home);
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e)
        {
            e.printStackTrace();

        }
    }
    public void search() throws SQLException, IOException
    {
        OperaMetadati operadati = new OperaMetadati("","","");
        txtsearch.setPromptText("");
        if(cbfilter.getValue().equals("Keyword"))       //controllo che nella choisebox sia stato scelto il filtro Keyword
        {
            String keywords = txtsearch.getText();
            oblist.removeAll(oblist);                   //Rimuovo tutte le eventuali e precedenti ricerche
            if (!keywords.equals(""))
            {
                ResultSet resultSet = (ResultSet) SearchOperaQuery.SearchOperaQueryKeyword(keywords);
                while (resultSet.next())
                {
                    //setto le variabili con le informazioni presenti nel DB e le passo al metodo setTable
                    autore = resultSet.getString("autore");
                    titolo = resultSet.getString("titolo");
                    genere = resultSet.getString("c.nome");
                    if(!operadati.getTitolo().equals(titolo) || !operadati.getAutore().equals(autore))
                    {
                        setTable(titolo, autore, genere);
                    }
                    operadati.setTitolo(titolo);
                    operadati.setAutore(autore);
                    operadati.setGenere(genere);
                }

            } else
                {
                txtsearch.setPromptText("Inserire almeno una lettera!");
                }
        }
        else
        {
            if(cbfilter.getValue().equals("Autore"))
            {
                String keywords = txtsearch.getText();
                oblist.removeAll(oblist);
                if (!keywords.equals(""))
                {
                    ResultSet resultSet = (ResultSet) SearchOperaQuery.SearchOperaQueryAutore(keywords);
                    while (resultSet.next())
                    {
                        autore = resultSet.getString("autore");
                        titolo = resultSet.getString("titolo");
                        genere = resultSet.getString("c.nome");
                        if(!operadati.getTitolo().equals(titolo) || !operadati.getAutore().equals(autore))
                        {
                            setTable(titolo, autore, genere);
                        }
                        operadati.setTitolo(titolo);
                        operadati.setAutore(autore);
                        operadati.setGenere(genere);
                    }

                } else
                    {
                    txtsearch.setPromptText("Inserire almeno una lettera!");
                    }
            }else
            {
                String keywords = txtsearch.getText();
                oblist.removeAll(oblist);
                if (!keywords.equals(""))
                {
                    ResultSet resultSet = (ResultSet) SearchOperaQuery.SearchOperaQueryGenere(keywords);
                    while (resultSet.next())
                    {
                        autore = resultSet.getString("autore");
                        titolo = resultSet.getString("titolo");
                        genere = resultSet.getString("c.nome");

                        if(!operadati.getTitolo().equals(titolo) || !operadati.getAutore().equals(autore))
                        {
                            setTable(titolo, autore, genere);
                        }
                        operadati.setTitolo(titolo);
                        operadati.setAutore(autore);
                        operadati.setGenere(genere);
                    }
                }
                else
                {
                    txtsearch.setText("Inserire almeno una lettera!");
                }
            }
        }
        txtsearch.clear();
    }

    public void returnlogin() throws IOException
    {
        try{
        Stage back;
        Parent gologin;

        back =(Stage) btlogout.getScene().getWindow();
        gologin = FXMLLoader.load(getClass().getResource("../view/Login.fxml"));
        back.setTitle("JLibrary");
        Scene homescene = new Scene(gologin);
        back.setScene(homescene);
        back.show();

        } catch (IOException e)
        {
            e.printStackTrace();

        }
    }

    public void gotoprofile(ActionEvent event) throws SQLException
    {
        ViewProfileController.ViewProfile(event);
    }

    public void gotochose(ActionEvent event) throws  SQLException
    {
        new UploadController().UploadController();
    }

    public void gotomanageuser(ActionEvent event) throws SQLException
    {
        GestioneUserController.GestioneUserController(event);
    }

    //metodo per inizializzare il ChoiseBox e la Table view
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        cbfilter.getItems().addAll("Autore","Genere","Keyword");
        cbfilter.setValue("Keyword");

        col_titolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        col_autore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        col_genere.setCellValueFactory(new PropertyValueFactory<>("genere"));
        col_link.setCellValueFactory(new PropertyValueFactory<>("view"));

        oblist=FXCollections.observableArrayList();
    }

    //Metodo per settare la Tableview con i valori presi dal DB
    private void setTable(String titolo, String autore, String genere) throws IOException
    {
        oblist.add(new OperaMetadati(titolo, autore, genere));
        tablesearch.setItems(oblist);
    }

}
