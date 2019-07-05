package io.springboot.pay.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "pay_product")
public class Product {
    @Id
    private String id;
    @Field(value = "name")
    private String name; //商品名称

    private String price; //产品单价

    private int inventory; //库存

    //正常这里应该有个商品描述 然而，我懒得写了。
}
