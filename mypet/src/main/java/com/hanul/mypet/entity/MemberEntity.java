package com.hanul.mypet.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name = "member")
public class MemberEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String area;
    
    private String cityArea;
    
    private String detailArea;

    @Column(length = 15)
    private String phone;

    private boolean fromSocial;

    private boolean verified;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AnimalEntity> adoptedAnimals = new HashSet<>(); // 매핑 설정 추가


    public void addMemberRole(MemberRole memberRole) {
        roleSet.add(memberRole);
    }
    
    public void adoptAnimal(AnimalEntity animalEntity) {
        adoptedAnimals.add(animalEntity);
    }
}


