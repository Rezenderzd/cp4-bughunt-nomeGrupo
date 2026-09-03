package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int idade;
    private double creditos;

    public Usuario() {
    }

    public Usuario(String nome, int idade, double creditos) {
        this.nome = nome;
        this.idade = idade;
        this.creditos = creditos;
    }

    public boolean temCreditosSuficientes(double preco) {
        return preco <= this.creditos;
    }

    public void debitarCreditos(double valor) {
        // adiciona o valor aos créditos do usuário
        this.creditos = this.creditos - valor;
    }

    public Usuario alugar(Conteudo conteudo) throws ClassificacaoIndicativaException {
        if(!conteudo.isDisponivel()){
            new ConteudoIndisponivelException("Conteudo está indisponível no moemento");
        }
        conteudo.setDisponivel(false);
        if (this.idade < conteudo.getClassificacaoEtaria()) {
            conteudo.setDisponivel(true);
            throw new ClassificacaoIndicativaException("Usuário de " + this.idade
                    + " anos não pode assistir a " + conteudo.getTitulo()
                    + " (classificação " + conteudo.getClassificacaoEtaria() + " anos)");
        }
        double precoPagar = Math.round(conteudo.calcularPrecoPromocional() * 100.0) / 100.0;

        if (!temCreditosSuficientes(precoPagar)) {
            conteudo.setDisponivel(true);
            throw new CreditosInsuficientesException("Créditos insuficientes para alugar " + conteudo.getTitulo());
        }

        debitarCreditos(precoPagar);
        conteudo.setDisponivel(false);

        System.out.println("==================================================");
        System.out.println("RECIBO STREAMFIAP");
        System.out.println("Usuario: " + this.nome);
        System.out.println("Conteudo: " + conteudo.getTitulo());
        System.out.println("Valor pago: R$ " + precoPagar);
        System.out.println("Creditos restantes: R$ " + this.creditos);
        System.out.println("Obrigado por usar o StreamFIAP!");
        System.out.println("==================================================");

        return this;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getCreditos() { return creditos; }
    public void setCreditos(double creditos) { this.creditos = creditos; }
}
