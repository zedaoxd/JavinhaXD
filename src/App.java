import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entities.Funcionario;

public class App {
    public static void main(String[] args) throws Exception {
        var funcionarios = seed();

        System.out.println("\n\ntodos os funcionários:");
        ListAll(funcionarios);

        removeFromName(funcionarios, "João");

        System.out.println("\n\ntodos os funcionários após remover João:");
        ListAll(funcionarios);

        changeSalaryPercent(funcionarios, new BigDecimal("1.1"));

        System.out.println("\n\ntodos os funcionários após aumento de 10%:");
        ListAll(funcionarios);

        System.out.println("\n\ntodos os funcionários agrupados por função:");
        groupByFunction(funcionarios);

        System.out.println("\n\nFuncionários que fazem aniversário em outubro e dezembro:");
        birthday(funcionarios, new int[] { 10, 12 });

        System.out.println("\n\nFuncionário mais velho:");
        var oldest = oldest(funcionarios);
        var idade = LocalDate.now().getYear() - oldest.getNascimento().getYear();
        System.out.println("Nome: " + oldest.getNome() + " | Idade: " + idade + " anos");

        System.out.println("\n\nFuncionários em ordem alfabética:");
        sortByName(funcionarios);
        ListAll(funcionarios);

        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
        var totalSalaries = totalSalaries(funcionarios);
        System.out.println("\n\nTotal dos salários: " + decimalFormat.format(totalSalaries));

        System.out.println("\n\nSalários em salários mínimos:");
        var salariesInMinimumWage = salariesInMinimumWage(funcionarios);
        System.out.println("Nome |\t Salário em salários mínimos");
        salariesInMinimumWage.forEach((nome, salario) -> {
            System.out.println(nome + " => " + decimalFormat.format(salario));
        });
    }

    private static List<Funcionario> seed() {
        return readCSV("src\\resources\\funcionarios.csv");
    }

    /**
     * Quantos salarios minimos cada funcionario recebe: salario minimo = 1212.00
     */
    private static Map<String, BigDecimal> salariesInMinimumWage(List<Funcionario> funcionarios) {
        var salarioMinimo = new BigDecimal("1212.00");
        int scale = 2;

        return funcionarios.stream()
                .collect(Collectors.toMap(Funcionario::getNome,
                        f -> f.getSalario().divide(salarioMinimo, scale, RoundingMode.HALF_UP)));
    }

    /**
     * Total dos salarios
     */
    private static BigDecimal totalSalaries(List<Funcionario> funcionarios) {
        return funcionarios
                .stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
     * Funcionarios em ordem alfabética
     */
    private static void sortByName(List<Funcionario> funcionarios) {
        funcionarios.sort((f1, f2) -> f1.getNome().compareTo(f2.getNome()));
    }

    /**
     * Pega o funcionário com maior idade
     */
    private static Funcionario oldest(List<Funcionario> funcionarios) {
        return funcionarios
                .stream()
                .min((f1, f2) -> f1.getNascimento().compareTo(f2.getNascimento()))
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    /**
     * Lista os funcionários que fazem aniversário nos meses informados
     */
    private static void birthday(List<Funcionario> funcionarios, int[] months) {
        System.out.println("=====================================");
        funcionarios
                .stream()
                .filter(f -> {
                    for (int month : months) {
                        if (f.getNascimento().getMonthValue() == month) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(System.out::println);
        System.out.println("=====================================");
    }

    /**
     * Agrupa os funcionários por função
     */
    private static void groupByFunction(List<Funcionario> funcionarios) {
        var grouped = funcionarios
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(Funcionario::getFuncao));

        System.out.println("=====================================");
        grouped.forEach((funcao, func) -> {
            System.out.println("Função: " + funcao);
            func.forEach(System.out::println);
            System.out.println("\n");
        });
    }

    /**
     * Altera o salário de todos os funcionários de acordo com o percentual
     */
    private static void changeSalaryPercent(List<Funcionario> funcionarios, BigDecimal percent) {
        funcionarios
                .forEach(f -> {
                    BigDecimal newSalary = f.getSalario().multiply(percent);
                    f.setSalario(newSalary);
                });
    }

    /**
     * Lista todos os funcionários
     */
    private static void ListAll(List<Funcionario> funcionarios) {
        System.out.println("=====================================");
        funcionarios.forEach(System.out::println);
        System.out.println("=====================================");
    }

    /**
     * Remove um funcionário pelo nome
     */
    private static Funcionario removeFromName(List<Funcionario> funcionarios, String nome) {
        var funcionario = funcionarios
                .stream()
                .filter(f -> f.getNome().equals(nome))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if (funcionario != null) {
            funcionarios.remove(funcionario);
        }

        return funcionario;
    }

    /**
     * Lê um arquivo CSV e retorna uma lista de funcionários
     */
    private static List<Funcionario> readCSV(String path) {
        var funcionarios = new ArrayList<Funcionario>();
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (var reader = new FileReader(path); var br = new BufferedReader(reader)) {
            String linha;
            br.readLine(); // skip header
            while ((linha = br.readLine()) != null) {
                var decodedLine = new String(linha.getBytes(), StandardCharsets.UTF_8);
                var fields = decodedLine.split(",");

                var nome = fields[0];
                var nascimento = fields[1];
                var salario = fields[2];
                var funcao = fields[3];

                var data = LocalDate.parse(nascimento, formatter);

                var funcionario = new Funcionario(nome, data, funcao, new BigDecimal(salario));

                funcionarios.add(funcionario);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return funcionarios;
    }
}
