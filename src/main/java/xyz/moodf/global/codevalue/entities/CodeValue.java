package xyz.moodf.global.codevalue.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(indexes = @Index(name = "idx_codevalue_category", columnList = "category"))
@NoArgsConstructor
@AllArgsConstructor
public class CodeValue implements Serializable {
    @Id
    @Column(name = "_code", length = 45)
    private String code;

    @Lob
    @Column(name = "_value")
    private String value;

    @Column(length = 45)
    private String category;
}
