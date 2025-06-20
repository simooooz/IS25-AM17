package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class ClientCargoHoldsComponent extends ClientComponent {

    private final int number;
    private final List<ColorType> goods;

    public ClientCargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors);
        this.number = number;
        this.goods = new ArrayList<>();
    }

    public List<ColorType> getGoods() {
        return goods;
    }

    @Override
    public List<String> icon() {
        String text;
        if (number == 2) {
            if (goods.size() == 1)
                text = goods.getFirst().toString() + "   " + Chroma.color("   " , getColor());
            else if (goods.size() == 2)
                text = goods.getFirst().toString() + "   " + goods.get(1).toString();
            else
                text = Chroma.color("  " , getColor()) + "   " + Chroma.color("  " , getColor());
        }
        else if (number == 3) {
            if (goods.size() == 1)
                text = goods.getFirst().toString() + " " + Chroma.color("  " , getColor()) + " " + Chroma.color("  " , getColor());
            else if (goods.size() == 2)
                text = goods.getFirst().toString() + " "   + goods.get(1).toString() + "  " + Chroma.color("  " , getColor());
            else if (goods.size() == 3)
                text = goods.getFirst().toString() + " "  + goods.get(1).toString() + " " + goods.get(2).toString();
            else
                text = Chroma.color("  " , getColor()) + " " + Chroma.color("  " , getColor()) + " " + Chroma.color("  " , getColor());
        }
        else {
            if (goods.size() == 1)
                text = goods.getFirst().toString();
            else
                text = Chroma.color("  " , getColor());
        }


        return new ArrayList<>(List.of(
                Constants.inTheMiddle(text, 11),
                "    " + goods.size() + "/" + number + "    "
        ));
    }

    public String getColor() {
        return Chroma.GREY_BACKGROUND;
    }

}