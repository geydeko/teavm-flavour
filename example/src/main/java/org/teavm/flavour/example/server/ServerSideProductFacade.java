/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.flavour.example.server;

import java.util.List;
import java.util.stream.Collectors;
import org.jinq.jpa.JPQL;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teavm.flavour.example.api.ProductDTO;
import org.teavm.flavour.example.api.ProductFacade;
import org.teavm.flavour.example.api.ProductQueryDTO;
import org.teavm.flavour.example.model.Product;
import org.teavm.flavour.example.model.ProductRepository;

/**
 *
 * @author Alexey Andreev
 */
@Component
public class ServerSideProductFacade implements ProductFacade {
    private ProductRepository repository;

    @Autowired
    public ServerSideProductFacade(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public int create(ProductDTO data) {
        Product product = fromDTO(data);
        repository.add(product);
        return repository.getId(product);
    }

    @Override
    public List<ProductDTO> list(ProductQueryDTO query) {
        JinqStream<Product> all = repository.all();
        if (query.namePart != null && !query.namePart.trim().isEmpty()) {
            all = all.where(product -> JPQL.like(product.getName().toLowerCase(),
                    "%" + query.namePart.toLowerCase() + "%"));
        }
        if (query.page.limit != 0) {
            all.limit(query.page.limit);
        }
        return all.skip(query.page.offset)
                .sortedBy(product -> product.getName())
                .toList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO get(int id) {
        return toDTO(requireProduct(id));
    }

    @Override
    public void update(int id, ProductDTO data) {
        fromDTO(data, requireProduct(id));
    }

    @Override
    public void delete(int id) {
        repository.remove(requireProduct(id));
    }

    private Product requireProduct(int id) {
        Product product = repository.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + id + " was not found");
        }
        return product;
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.name = product.getName();
        dto.sku = product.getSku();
        dto.unitPrice = product.getPrice();
        return dto;
    }

    private Product fromDTO(ProductDTO data) {
        return new Product(data.sku, data.name, data.unitPrice);
    }

    private void fromDTO(ProductDTO data, Product product) {
        product.setSku(data.sku);
        product.setName(data.name);
        product.setPrice(data.unitPrice);
    }
}
