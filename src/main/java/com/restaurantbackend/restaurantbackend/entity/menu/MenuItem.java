package com.restaurantbackend.restaurantbackend.entity.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.restaurantbackend.restaurantbackend.entity.department.Department;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private String description;

    @Column(name = "is_available")
    @JsonProperty("available")
    private boolean isAvailable;

    private String imageUrl;

    // Besin değerleri
    @Column(name = "calories")
    private Integer calories;

    @Column(name = "protein", precision = 5, scale = 2)
    private BigDecimal protein;

    @Column(name = "carbs", precision = 5, scale = 2)
    private BigDecimal carbs;

    @Column(name = "fat", precision = 5, scale = 2)
    private BigDecimal fat;

    // Ürün özellikleri
    @Column(name = "preparation_time")
    private Integer preparationTime; // dakika

    @Column(name = "spice_level")
    private Integer spiceLevel; // 1-5 arası

    @Column(name = "is_vegetarian")
    private Boolean isVegetarian;

    @Column(name = "is_vegan")
    private Boolean isVegan;

    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree;

    // İçerik bilgileri
    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "allergens", columnDefinition = "TEXT")
    private String allergens;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    @JsonIgnore
    private Subcategory subcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    public MenuItem() {}

    public MenuItem(Long id, String name, BigDecimal price, String description, boolean isAvailable, String imageUrl, 
                   Integer calories, BigDecimal protein, BigDecimal carbs, BigDecimal fat, Integer preparationTime, 
                   Integer spiceLevel, Boolean isVegetarian, Boolean isVegan, Boolean isGlutenFree, 
                   String ingredients, String allergens, Subcategory subcategory, Department department) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.preparationTime = preparationTime;
        this.spiceLevel = spiceLevel;
        this.isVegetarian = isVegetarian;
        this.isVegan = isVegan;
        this.isGlutenFree = isGlutenFree;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.subcategory = subcategory;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // Besin değerleri getter/setter
    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }

    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    // Ürün özellikleri getter/setter
    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Integer getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(Integer spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }

    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    // İçerik bilgileri getter/setter
    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}