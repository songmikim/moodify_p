package xyz.moodf.global.codevalue.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CodeValue implements Serializable {
    @Id
    @Column(name = "_code", length = 45)
    private String code;

    @Lob
    @Column(name = "_value")
    private String value;
}
