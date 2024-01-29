package com.study.vip.mall.cart.mapper;

import com.study.vip.mall.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartMapper extends MongoRepository<Cart,String> {
}
