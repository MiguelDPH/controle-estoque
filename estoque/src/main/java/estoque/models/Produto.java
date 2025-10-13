package estoque.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome não pode ser nulo ou vazio.")
    private String nome;

    private String codigo;

    private String descricao;

    @NotNull(message = "O valor unitário é obrigatório.")
    @Min(value = 0, message = "O valor deve ser maior ou igual a zero.")
    private Float valorUnidade;

    @NotNull(message = "A quantidade inicial é obrigatória.")
    @Min(value = 0, message = "A quantidade deve ser maior ou igual a zero.")
    private Integer quantidadeEstoque;

    @NotNull(message = "O estoque mínimo é obrigatório.")
    @Min(value = 0, message = "O estoque mínimo deve ser maior ou igual a zero.")
    private Integer estoqueMinimo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "A categoria é obrigatória.")
    private Categoria categoria;

    public Produto() {
        this.quantidadeEstoque = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Float getValorUnidade() { return valorUnidade; }
    public void setValorUnidade(Float valorUnidade) { this.valorUnidade = valorUnidade; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}