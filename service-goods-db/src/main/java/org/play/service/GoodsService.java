package org.play.service;

import org.play.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsService extends JpaRepository<Goods, Long> {
}
