package com.graphhopper.jsprit.core.reporting.route;

import java.util.function.Consumer;

import com.graphhopper.jsprit.core.problem.solution.route.activity.AbstractActivity;
import com.graphhopper.jsprit.core.reporting.AbstractPrinterColumn;
import com.graphhopper.jsprit.core.reporting.columndefinition.ColumnDefinition;
import com.graphhopper.jsprit.core.reporting.columndefinition.StringColumnType;

/**
 * The type of the activity.
 *
 * @author balage
 *
 */
public class ActivityTypePrinterColumn extends AbstractPrinterColumn<RoutePrinterContext, String, ActivityTypePrinterColumn> {

    /**
     * Constructor.
     */
    public ActivityTypePrinterColumn() {
        super();
    }

    /**
     * Constructor with a post creation decorator provided.
     */
    public ActivityTypePrinterColumn(Consumer<ColumnDefinition.Builder> decorator) {
        super(decorator);
    }

    @Override
    public ColumnDefinition.Builder getColumnBuilder() {
        return new ColumnDefinition.Builder(new StringColumnType());
    }

    @Override
    public String getData(RoutePrinterContext context) {
        return ((AbstractActivity) context.getActivity()).getType();
    }

    @Override
    protected String getDefaultTitle() {
        return "activity";
    }

}