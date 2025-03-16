package io.nssa.iteams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "category_metadata")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    
    @Column(nullable = false)
    private Byte level;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 4)
    private String code;
    
    @Column
    private Long parentId;
}