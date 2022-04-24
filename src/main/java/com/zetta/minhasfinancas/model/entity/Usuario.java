package com.zetta.minhasfinancas.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "usuario", schema = "financas" )
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * pode omitir o @Column, já que o nome é
     * o mesmo, mas é boa prática manter
     */
    @Id
    @Column(name = "id")
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;
}
