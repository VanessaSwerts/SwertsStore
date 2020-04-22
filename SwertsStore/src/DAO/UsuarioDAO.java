package DAO;

/**
*    CLASSE USUARIO DAO  
*   Classe para realizar a conexão com o servidor do BANCO DE DADOS, afim de armazenar os dados do usuário.  
*    
*   @since Maio/2019 
*       
*/

import model.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class UsuarioDAO {

    // Objeto responsável pela conexão com o servidor do banco de dados
    Connection con;
    // Objeto responsável por preparar as consultas DINAMICAS
    PreparedStatement pst;
    // Objeto responsável por executar as consultas ESTÁTICAS
    Statement st;
    // Objeto responsável por referencia a tabela resultante da busca
    ResultSet rs;
    
    // Nome do banco que irá fazer a conexão. 
    String database = "swerts_db";
    // URL do Banco de Dados 
    String url = "jdbc:mysql://localhost:3306/" + database + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
    //Nome do usuario no Banco de Dados
    String user = "root";
    //Senha do usuario no Banco de Dados
    String password = "root";
    //Flag para confirmação da conexão
    boolean sucesso = false;
    
    /**
     *  Método responsável por fazer a conexão com o Banco de Dados
     * 
     */
    public void connectToDb()
    {
        try 
        {   
            con = DriverManager.getConnection(url, user, password);

        } catch (SQLException ex) 
        {

            System.out.println("Erro: " + ex.getMessage());

        }
    }

    /**
     * Método responsável por inserir usuarios no Banco de Dados
     * @param novoUsuario do tipo Usuário 
     * @return sucesso, uma varivável de controle para saber se deu certo. 
     */
    public boolean inserirUsuario(Usuario novoUsuario) {
        connectToDb();

        String sql = "INSERT INTO usuario (nome, sobrenome, cpf, sexo, data_de_nascimento, telefone, email, senha,"
                + "rua, bairro, numero, complemento, cep, cidade, estado) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try 
        {
            pst = con.prepareStatement(sql);
            
            pst.setString(1, novoUsuario.getNome());
            pst.setString(2, novoUsuario.getSobrenome());
            pst.setString(3, novoUsuario.getCpf());
            pst.setString(4, novoUsuario.getSexo());
            pst.setString(5, novoUsuario.getDataDeNascimento());
            pst.setString(6, novoUsuario.getTelefone());
            pst.setString(7, novoUsuario.getEmail());
            pst.setString(8, novoUsuario.getSenha());
            pst.setString(9, novoUsuario.getRua());
            pst.setString(10, novoUsuario.getBairro());
            pst.setInt(11, novoUsuario.getNumero());
            pst.setString(12, novoUsuario.getComplemento());
            pst.setString(13, novoUsuario.getCep());
            pst.setString(14, novoUsuario.getCidade());
            pst.setString(15, novoUsuario.getEstado());
            pst.execute();

            sucesso = true;

        } catch (SQLException ex) 
        {
            System.out.println("Erro = " + ex.getMessage());
            sucesso = false;
        } finally {
            try 
            {
                con.close();
                pst.close();
            } catch (SQLException ex) 
            {
                System.out.println("Erro = " + ex.getMessage());
            }

        }
        
        if(sucesso)
            criarCarrinho(novoUsuario.getEmail());
        
        return sucesso;
    }
    /**
     * Método responsável por criar carrinho para o novo usuário. 
     * @param email do tipo String, recebe o email do novo Usuário. 
     * 
     */
    public void criarCarrinho(String email) {
        
        connectToDb();

        String sql = "INSERT INTO carrinho (email_usuario) values (?)";

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.execute();

            sucesso = true;
            JOptionPane.showMessageDialog(null, "Cadastro finalizado com sucesso!", "Swerts Store", 1);

        } catch (SQLException ex) {
            System.out.println("Erro = " + ex.getMessage());
            sucesso = false;
        } finally {
            try {
                con.close();
                pst.close();
            } catch (SQLException ex) {
                System.out.println("Erro = " + ex.getMessage());
            }

        }
    }

    /**
     * Método do tipo ArrayList para buscar usuarios no Banco de Dados
     * @return listaDeUsuarios
     */
    public ArrayList<Usuario> buscarUsuariosSemFiltro() 
    {
        ArrayList<Usuario> listaDeUsuarios = new ArrayList<>();

        connectToDb();

        String sql = "SELECT * FROM usuario";

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            
            while (rs.next()) 
            {
                Usuario usuarioTemp = new Usuario(rs.getString("nome"), rs.getString("sobrenome"), rs.getString("cpf"), rs.getString("sexo"),
                        rs.getString("data_de_nascimento"), rs.getString("telefone"), rs.getString("email"), rs.getString("senha"),
                        rs.getString("rua"), rs.getString("bairro"), rs.getInt("numero"), rs.getString("complemento"),
                        rs.getString("cep"), rs.getString("cidade"), rs.getString("estado"));
               
                listaDeUsuarios.add(usuarioTemp);

            }
            sucesso = true;

        } catch (SQLException ex) {
            System.out.println("Erro = " + ex.getMessage());
            sucesso = false;
        } finally {
            try {
                con.close();
                st.close();
            } catch (SQLException ex) {
                System.out.println("Erro = " + ex.getMessage());
            }

        }
        return listaDeUsuarios;
    }

    /**
     * Método para buscar usuarios no Banco de Dados pelo email
     * @param emailUsuario
     * @return usuarioOnline que recebe as informações do Banco de Dados
     */
    public Usuario buscarUsuarioPeloEmail(String emailUsuario) {
        
        Usuario usuarioOnline = new Usuario();

        connectToDb();

        String sql = "SELECT * FROM usuario WHERE email = '" + emailUsuario + "'";

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                // Guardando informações retornadas do banco na classe usuario
                Usuario usuarioTemporario = new Usuario(rs.getString("nome"), rs.getString("sobrenome"), rs.getString("cpf"),
                        rs.getString("sexo"), rs.getString("data_de_nascimento"), rs.getString("telefone"),
                        rs.getString("email"), rs.getString("senha"), rs.getString("rua"), rs.getString("bairro"),
                        rs.getInt("numero"), rs.getString("complemento"), rs.getString("cep"),
                        rs.getString("cidade"), rs.getString("estado"));
                usuarioOnline = usuarioTemporario; 
            }
            sucesso = true;

        } catch (SQLException ex) {
            System.out.println("Erro = " + ex.getMessage());
            sucesso = false;
        } finally {
            try {
                con.close();
                st.close();
            } catch (SQLException ex) {
                System.out.println("Erro = " + ex.getMessage());
            }

        }

        return usuarioOnline;
    }

    /**
     * Método para atualizar usuarios no Banco de Dados
     * @param novoUsuario do tipo Usuario 
     * @param email do tipo String, para saber qual usuário será atualizado 
     * @return 
     */
    public boolean alterarUsuario(Usuario novoUsuario, String email) {
        connectToDb();

        String sql = "UPDATE usuario SET nome = ?, sobrenome = ?, cpf = ?, sexo = ?, data_de_nascimento = ?, telefone = ?, "
                + "email = ?, senha = ?, rua = ?, bairro = ?, numero = ?, complemento = ?, cep = ?, cidade = ?, estado = ? "
                + "WHERE email = ?;";

        try {
            pst = con.prepareStatement(sql);

            pst.setString(1, novoUsuario.getNome());
            pst.setString(2, novoUsuario.getSobrenome());
            pst.setString(3, novoUsuario.getCpf());
            pst.setString(4, novoUsuario.getSexo());
            pst.setString(5, novoUsuario.getDataDeNascimento());
            pst.setString(6, novoUsuario.getTelefone());
            pst.setString(7, novoUsuario.getEmail());
            pst.setString(8, novoUsuario.getSenha());
            pst.setString(9, novoUsuario.getRua());
            pst.setString(10, novoUsuario.getBairro());
            pst.setInt(11, novoUsuario.getNumero());
            pst.setString(12, novoUsuario.getComplemento());
            pst.setString(13, novoUsuario.getCep());
            pst.setString(14, novoUsuario.getCidade());
            pst.setString(15, novoUsuario.getEstado());
            pst.setString(16, email);
            pst.execute();

            sucesso = true;
            JOptionPane.showMessageDialog(null, "Seus dados foram atualizados com suecesso!", "Swerts Store", 1);

        } catch (SQLException ex) {
            System.out.println("Erro = " + ex.getMessage());
            sucesso = false;
        } finally {
            try {
                con.close();
                pst.close();
            } catch (SQLException ex) {
                System.out.println("Erro = " + ex.getMessage());
            }

        }

        return sucesso;
    }
}
