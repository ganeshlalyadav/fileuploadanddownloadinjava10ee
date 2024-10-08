package org.test.jakarta.hello.repository;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.test.jakarta.hello.dto.ProductDto;
import org.test.jakarta.hello.model.Product;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProductRepository {

    @PersistenceContext(unitName = "productPU")

    private EntityManager em;

    public void create(Product product) {
        em.persist(product);
    }

    public Product find(Long id) {
        return em.find(Product.class, id);
    }

    public Product update(Product product) {
        return em.merge(product);
    }

    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
    public List<ProductDto> getAllProduct() {
        List<Product> productList= em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        return productList.stream().map(product -> new ProductDto(product.getId(),product.getName(),product.getPrice())).collect(Collectors.toList());
    }
    public List<Product> filterByRSQL(Node rootNode) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = builder.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        if (rootNode instanceof ComparisonNode) {
            ComparisonNode comparisonNode = (ComparisonNode) rootNode;

            System.out.println("Selector: " + comparisonNode.getSelector());

            if (comparisonNode.getOperator().equals(RSQLOperators.EQUAL)) {
                if (comparisonNode.getSelector().equals("name")) {
                    System.out.println("Filter Value: " + comparisonNode.getArguments().get(0));

                    query.where(builder.equal(root.get("name"), comparisonNode.getArguments().get(0)));
                }
            }
        }

        return em.createQuery(query).getResultList();
    }






}
