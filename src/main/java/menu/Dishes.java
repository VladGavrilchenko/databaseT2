package menu;

import javax.persistence.*;

@Entity
@Table(name="RestaurantMenu")
public class Dishes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column (name = "id")
    private  long id;

    @Column(nullable = false)
    private  String nameDishes;

    @Column(nullable = false)
    private Double  price;

    @Column(nullable = false)
    private Double  weight;

    @Column(nullable = false)
    private boolean isSell;


    private double discount;


    public Dishes(){}

    public Dishes(String nameDishes , Double  price , Double  weight , boolean isSell) {
        this.nameDishes = nameDishes;
        this.price = price;
        this.weight = weight;
        this.isSell = isSell;


    }


    public Dishes(String nameDishes , Double  price , Double  weight , boolean isSell , double discount) {
        this.nameDishes = nameDishes;
        this.price = price;
        this.weight = weight;
        this.isSell = isSell;
        this.discount = discount;
    }



    public void setADiscountToPrice( long discount ) {
        if ( discount <= 100 && discount >= 0) {
            setDiscount(discount);
            setPrice((this.price * discount) / 100);
        }

        else
            System.out.println("No correct discount");



    }

    public void removeADiscountToPrice() {
        if(this.discount != 0) {
            setPrice((this.price * 100) / this.discount);
            setDiscount(0);
        }

    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isSell() {
        return isSell;
    }


    public Double getPrice() {
        return price;
    }

    public Double getWeight() {
        return weight;
    }

    public String getNameDishes() {
        return nameDishes;
    }

    public void setNameDishes(String nameDishes) {
        this.nameDishes = nameDishes;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSell(boolean sell) {
        isSell = sell;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public  String toString(){
        return  "Id: "+ id + ", dishes name: "+ nameDishes + " , weight= "+  weight +  " , price= " + price +
                " , discount= "+  discount + "% , is sell: " + isSell;
    }
}
