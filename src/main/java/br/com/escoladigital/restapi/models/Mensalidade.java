package br.com.escoladigital.restapi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "MENSALIDADE")
@Getter
@Setter
public class Mensalidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rematricula_id")
    private Rematricula rematricula;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "mes_ano", nullable = false)
    private String mesAno;

    @Column(nullable = false)
    private Integer vencimento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

}
