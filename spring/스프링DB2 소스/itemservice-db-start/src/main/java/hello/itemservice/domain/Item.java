package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@Table(name= "item") 객체명이랑 테이블명이 같으면 생략 가능
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 값을 넣는 전략
    private Long id;
    @Column(name="item_name", length=10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
