package DAO;

/*
    *** CLASSE CARRINHO DAO ***

Objetivo: Criar objeto para conectar com o servidor do BANCO DE DADOS, afim de armazenar os dados do carrinho.  

*/

import model.Carrinho;
import model.Produto;
import model.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class CarrinhoDAO {
    
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

    //Método para realizar a conexão com o banco de dados
    public void connectToDb() {
        try {
            con = DriverManager.getConnection(url, user, password);

        } catch (SQLException ex) {

            System.out.println("Erro: " + ex.getMessage());

        }
    }

    // Metodo para verificar se um produto ja se encontra no carrinho do usuario
    public boolean verificaProduto(Usuario usuarioOnline, Produto produto, int quantidade) {
        connectToDb();
        Integer existeNoCarrinho = -1;
        CallableStatement callableStatement = null;

        try {

            callableStatement = con.prepareCall("{CALL verificaProduto(" + produto.getCodigo() + ", '"
                    + usuarioOnline.getEmail() + "', ?)}");

            callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);
            callableStatement.execute();

            existeNoCarrinho = callableStatement.getInt(1);

        } catch (SQLException ex) {
            System.out.println("Erro = " + ex.getMessage());

        } finally {
            try {
                con.close();

            } catch (SQLException ex) {
                System.out.println("Erro = " + ex.getMessage());
            }
        }

        if (existeNoCarrinho == 0) {
            inserirProdutoCarrinho(usuarioOnline, produto, quantidade);
            sucesso = true;
        } else if (existeNoCarrinho == 1) {          
            sucesso = false;
        } else {
            JOptionPane.showMessageDialog(null, "Erro ao inserir produto no carrinho, por favor tente novamente!", "Swerts Store", 0);
            sucesso = false;
        }

        return sucesso;
    }

    // Inserir produtos no carrinho do Banco de Dados
    public boolean inserirProdutoCarrinho(Usuario usuarioOnline, Produto produto, int quantidade) {
        connectToDb();

        String sql = "INSERT INTO carrinho_has_produto (email_usuario, codigo_produto, quantidade) VALUES (?,?,?)";

        try {
            pst = con.prepareStatement(sql);

            pst.setString(1, usuarioOnline.getEmail());
            pst.setInt(2, produto.getCodigo());
            pst.setInt(3, quantidade);
            pst.execute();

            sucesso = true;

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

    // Buscar produtos no carrinho do Banco de Dados
    public Carrinho buscarProdutosNoCarrinho(Usuario usuarioOnline) {
        
        Carrinho carrinho = new Carrinho();

        connectToDb();

        String sql = "SELECT produto.*, carrinho_has_produto.quantidade AS quantidadeCarrinho FROM carrinho_has_produto \n"
                + "INNER JOIN produto ON carrinho_has_produto.codigo_produto = produto.codigo "
                + "&& carrinho_has_produto.email_usuario = '" + usuarioOnline.getEmail() + "'";

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                //System.out.println(rs.getInt("codigo"));
                Produto produtoTemp = new Produto(rs.getInt("codigo"), rs.getString("tipo"), rs.getFloat("preco"),
                        rs.getString("marca"), rs.getString("modelo"), rs.getString("cor"), rs.getString("titulo"),
                        rs.getInt("quantidade"), rs.getBytes("imagem"), rs.getInt("quantidadeCarrinho"));
               
                carrinho.add(produtoTemp);
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
        return carrinho;
    }

    // Atualizar carrinho no Banco de Dados
    public boolean alterarProdutoCarrinho(Usuario usuarioOnline, Produto produto, int quantidade) {
        connectToDb();

        String sql = "UPDATE carrinho_has_produto SET quantidade = ? WHERE email_usuario = '" + usuarioOnline.getEmail() + "'"
                + " && codigo_produto = " + produto.getCodigo();

        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, quantidade);

            pst.execute();

            sucesso = true;

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

    // Deletar produto do carrinho no Banco de Dados
    public boolean deletarProdutoCarrinho(Usuario usuarioOnline, Produto produto) {
        connectToDb();

        String sql = "DELETE FROM carrinho_has_produto WHERE email_usuario = '" + usuarioOnline.getEmail()
                + "' && codigo_produto = " + produto.getCodigo();

        try {
            pst = con.prepareStatement(sql);

            pst.execute();

            sucesso = true;

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
