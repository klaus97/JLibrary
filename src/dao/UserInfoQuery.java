package dao;

import dao.Interface.UserInfoInterface;
import model.InfoUserTable;
import model.UserModel;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class UserInfoQuery implements UserInfoInterface
{
    ConnectionClass connectionClass = new ConnectionClass();
    Connection connection = connectionClass.getConnection();
    PreparedStatement st;
    public UserInfoQuery()
    {
    }

    @Override
    public ResultSet UserInfoQuery(String user) throws SQLException
    {

        //preparo la query da inviare ed eseguire sul DB
        String sql = "SELECT username,password,email,nome,cognome,livello,trascrittore,vip,r.privilegio FROM utente u JOIN ruolo r ON u.ID=r.IDutente where u.username=?";
        st = connection.prepareStatement(sql);
        st.setString(1,user);
        //ritorno il sisultato della query
        ResultSet resultSet = st.executeQuery();

        while(resultSet.next())
        {
            UserModel usermodel = UserModel.getInstance();
            usermodel.setUsername(resultSet.getString("username"));
            usermodel.setPassword(resultSet.getString("password"));
            usermodel.setVip(resultSet.getBoolean("vip"));
            usermodel.setPrivilegio(resultSet.getString("r.privilegio"));
            usermodel.setLivello(resultSet.getInt("livello"));
            usermodel.setTrascrittore(resultSet.getBoolean("trascrittore"));
            usermodel.setNome(resultSet.getString("nome"));
            usermodel.setCognome(resultSet.getString("cognome"));
            usermodel.setEmail(resultSet.getString("email"));
        }
        return  resultSet;
    }

    @Override
    public int UpdateInfo(UserModel user) throws SQLException
    {

        //preparo la query da inviare ed eseguire sul DB
        String sql = "UPDATE utente SET password =?, email=?, nome=?, cognome =? WHERE username=?";
        st =connection.prepareStatement(sql);
        st.setString(1,user.getPassword());
        st.setString(2,user.getEmail());
        st.setString(3,user.getNome());
        st.setString(4,user.getCognome());
        st.setString(5,user.getUsername());

        //ritorno il sisultato della query
        int resultSet = st.executeUpdate();

        return resultSet;
    }

    @Override
    public void VipQuery(UserModel user) throws SQLException
    {
        int vip=0;
        if(user.isVip()==true){vip=1;}

        //preparo la query da inviare ed eseguire sul DB
        String sql = "UPDATE utente SET vip=? WHERE username=?";
        st = connection.prepareStatement(sql);
        st.setInt(1,vip);
        st.setString(2,user.getUsername());
        st.executeUpdate();

    }

    @Override
    public void Trascrittore(UserModel user) throws SQLException
    {
        int rictrascr=0;
        if(user.getRictrascrittore()==true){rictrascr=1;}

        //preparo la query da inviare ed eseguire sul DB
        String sql = "UPDATE utente SET ric_trascrittore=? WHERE username=?";
        st = connection.prepareStatement(sql);
        st.setInt(1,rictrascr);
        st.setString(2,user.getUsername());
        st.executeUpdate();
    }

    @Override
    public ArrayList<InfoUserTable> GetListUser(String key) throws SQLException
    {
        ArrayList<InfoUserTable> userlist= new ArrayList<>();
        String sql = "SELECT username,email,nome,cognome,r.privilegio FROM utente JOIN ruolo r ON(utente.ID=r.IDutente) WHERE username LIKE ?";
        st = connection.prepareStatement(sql);
        st.setString(1,"%"+key+"%");
        //ritorno il sisultato della query
        ResultSet resultSet = st.executeQuery();

        while (resultSet.next())
        {
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String nome = resultSet.getString("nome");
            String cognome = resultSet.getString("cognome");
            String privilegio =resultSet.getString("r.privilegio");

            try {
                InfoUserTable user = new InfoUserTable(username,privilegio,nome,cognome,email);
                userlist.add(user);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  userlist;
    }

    @Override
    public void DeleteUser(InfoUserTable deluser) throws SQLException
    {
        String sql = "DELETE FROM utente WHERE username = ?";
        st=connection.prepareStatement(sql);
        st.setString(1,deluser.getUsername());
        st.execute();
    }

    @Override
    public void AcceptTrascrittore(InfoUserTable user) throws SQLException
    {
        String sql = "UPDATE utente SET trascrittore=? WHERE( username = ?)";
        st=connection.prepareStatement(sql);
        st.setInt(1,1);
        st.setString(2,user.getUsername());
        st.execute();
    }

    @Override
    public void PromoteUser(InfoUserTable user) throws SQLException
    {
        String sql = "UPDATE utente inner join ruolo r ON(utente.ID=r.IDutente) SET privilegio=? WHERE(username = ? AND r.privilegio !=?)";
        st=connection.prepareStatement(sql);
        st.setString(1,"supervisor");
        st.setString(2,user.getUsername());
        st.setString(3,"supervisor");
        st.execute();
    }

    @Override
    public void RetrocediUser(InfoUserTable user) throws SQLException
    {
        String sql = "UPDATE utente JOIN ruolo ON(utente.ID=ruolo.IDutente) SET trascrittore=?,ric_trascrittore=? WHERE(username=? AND ruolo.privilegio !=?)";
        st=connection.prepareStatement(sql);
        st.setInt(1,0);
        st.setInt(2,2);
        st.setString(3,user.getUsername());
        st.setString(4,"supervisor");
        st.execute();
    }

    @Override
    public  ArrayList<InfoUserTable>  SupervisorUserPanelQuery (String keyword , String kind) throws SQLException
    {
        ArrayList<InfoUserTable>listuser = new ArrayList<>();

        if(kind.equals("ric_trascrittore"))
        {
            //preparo la query da inviare ed eseguire sul DB
            String sql1 = "SELECT username,nome,cognome,email,r.privilegio FROM utente JOIN ruolo r ON(utente.ID=r.IDutente) WHERE (ric_trascrittore=? AND trascrittore=? AND r.privilegio!=?)";
            st = connection.prepareStatement(sql1);
            st.setInt(1, 1);
            st.setInt(2, 0);
            st.setString(3,"admin");
            ResultSet resultSet = st.executeQuery();

            while (resultSet.next())
            {
                try {
                    listuser.add(new InfoUserTable(resultSet.getString("username"),resultSet.getString("r.privilegio"),resultSet.getString("nome"),resultSet.getString("cognome"),resultSet.getString("email")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return listuser;
        }
        else
        {
            if(kind.equals("Trascrittori"))
            {
                String sql1 = "SELECT username,nome,cognome,email,r.privilegio FROM utente JOIN ruolo r ON(utente.ID=r.IDutente) WHERE (trascrittore=? AND r.privilegio!=?)";
                st = connection.prepareStatement(sql1);
                st.setInt(1, 1);
                st.setString(2,"admin");
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next())
                {
                    try {
                        listuser.add(new InfoUserTable(resultSet.getString("username"),resultSet.getString("r.privilegio"),resultSet.getString("nome"),resultSet.getString("cognome"),resultSet.getString("email")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return listuser;
            }
            else
            {
                if(kind.equals(""))
                {
                    String sql1 = "SELECT username,nome,cognome,email,r.privilegio FROM utente JOIN ruolo r ON(utente.ID=r.IDutente) WHERE (r.privilegio!=?)";
                    st = connection.prepareStatement(sql1);
                    st.setString(1, "admin");
                    ResultSet resultSet = st.executeQuery();
                    while (resultSet.next())
                    {
                        String username = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String nome = resultSet.getString("nome");
                        String cognome = resultSet.getString("cognome");
                        String privilegio =resultSet.getString("r.privilegio");

                        try {
                            InfoUserTable user = new InfoUserTable(username,privilegio,nome,cognome,email);
                            listuser.add(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return listuser;
                }
            }
        }
        return null;
    }

    @Override
    public  void setSupervisorQuery(InfoUserTable supervisor) throws SQLException
    {
        String sql="UPDATE ruolo inner join utente u on ruolo.IDutente=u.ID SET privilegio = ? WHERE u.username = ? " ;
        st=connection.prepareStatement(sql);
        st.setString(1,"supervisor");
        st.setString(2,supervisor.getUsername());

        st.executeUpdate();
    }

    @Override
    public  void setUserQuery(InfoUserTable user) throws SQLException
    {
        String sql="UPDATE ruolo inner join utente u on ruolo.IDutente=u.ID SET privilegio = ? WHERE u.username = ? " ;
        st=connection.prepareStatement(sql);
        st.setString(1,"utente base");
        st.setString(2, user.getUsername());

        st.executeUpdate();
    }

    @Override
    public  void setAdminQuery(InfoUserTable admin) throws SQLException
    {
        String sql="UPDATE ruolo inner join utente u on ruolo.IDutente=u.ID SET privilegio =? WHERE u.username=? " ;
        st=connection.prepareStatement(sql);
        st.setString(1,"admin");
        st.setString(2,admin.getUsername());

        st.executeUpdate();
    }
}
