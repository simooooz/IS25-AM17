package it.polimi.ingsw.model.components;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class SpecialCargoHoldsComponent extends Component {

    private final int number;
    private final List<ColorType> goods;

    public SpecialCargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors);
        this.number = number;
        this.goods = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public List<ColorType> getGoods() {
        return goods;
    }

    public void loadGood(ColorType good, Ship ship) {
        if (number == goods.size()) throw new GoodNotValidException("Cargo hold is full");
        goods.add(good);
        ship.getGoods().put(good, ship.getGoods().get(good) + 1);
    }

    public void unloadGood(ColorType good, Ship ship) {
        if (goods.isEmpty() || !goods.contains(good)) throw new GoodNotValidException("Cargo hold is empty");
        goods.remove(good);
        ship.getGoods().put(good, ship.getGoods().get(good) - 1);
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        for (ColorType good : goods) {
            ship.getGoods().put(good, ship.getGoods().get(good) - 1);
        }
    }

    @Override
    public List<String> icon() {
        String text = "";
        if (getNumber() == 2) {
            if (goods.size() == 1)
                text = " " + "\u2009" + "\u200A" + goods.getFirst().toString() + "  " + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + " ";
            else if (goods.size() == 2)
                text = " " + "\u2009" + "\u200A" + goods.getFirst().toString() + "  " + goods.get(1).toString() + "\u2009" + "\u200A" + " ";
            else
                text = " " + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "  " + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + " ";
        }
        else if (getNumber() == 3) {
            if (goods.size() == 1)
                text = " " + goods.getFirst().toString() + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + " ";
            else if (goods.size() == 2)
                text =  " " + goods.getFirst().toString() + "\u2009" + "\u200A"  + " " + goods.get(1).toString() + "\u2009" + "\u200A"+ Chroma.color("  " , Chroma.WHITE_BACKGROUND) + " ";
            else if (goods.size() == 3)
                text =  " " + goods.getFirst().toString() + "\u2009" + "\u200A"  + " " + goods.get(1).toString() + "\u2009" + "\u200A"+ goods.get(2).toString() + " ";
            else
                text = " " + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + " ";
        }
        else {
            if (goods.size() == 1)
                text = "   " + "\u2009" + "\u200A" + goods.getFirst().toString() + "\u2009" + "\u200A" + "   ";
            else
                text = "   " + "\u2009" + "\u200A" + Chroma.color("  " , Chroma.WHITE_BACKGROUND) + "\u2009" + "\u200A" + "   ";;
        }

        return new ArrayList<>(List.of(
            Constants.repeat(" ", 9),
            text,
            "   " + goods.size() + "/" + getNumber() + "   "
        ));
    }

}