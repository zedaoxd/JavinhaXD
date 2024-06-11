package entities;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Funcionario extends Pessoa {
    private BigDecimal salario;
    private String funcao;

    public Funcionario(String nome, LocalDate nascimento, String funcao, BigDecimal salario) {
        super(nome, nascimento);
        this.funcao = funcao;
        this.salario = salario;
    }

    public Funcionario() {
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getFuncao() {
        return funcao;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");

        return "Nome: " + this.getNome()
                + "\t| Nascimento: " + this.getNascimento().format(dateTimeFormatter)
                + "\t| Função: " + this.getFuncao()
                + "\t| Salário: " + decimalFormat.format(this.getSalario());
    }
}
