package com.psas.function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.psas.cbws.CBWS;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static com.psas.Main.promptFloatResponse;
import static com.psas.Main.promptIntegerResponse;
import static com.psas.translator.Translator.getFloatHex;
import static com.psas.translator.Translator.getHexFloat;

public class Function {
    /** Generic string for unknown attributes. */
    protected static final String UNKNOWN = "Unknown";

    /** Hex string for setting numerical attribute. */
    protected static final String NUMERICAL_ATTRIBUTE1 = "77300004";

    /** Hex string for setting numerical attribute. */
    protected static final String NUMERICAL_ATTRIBUTE2 = "90070004";

    /** Hex string for setting hit reaction attribute. */
    protected static final String HIT_REACTION = "48A40004";


    /** Hex string for setting hit volume length. */
    protected static final String HIT_VOLUME_LENGTH = "000D06D19B84001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume height. */
    protected static final String HIT_VOLUME_HEIGHT = "ECEE0E0C001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume x offset. */
    protected static final String HIT_VOLUME_X_OFFSET = "FE85D7C9001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume y offset. */
    protected static final String HIT_VOLUME_Y_OFFSET = "8982E75F001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume horizontal knock back. */
    protected static final String HIT_VOLUME_HORIZONTAL_KNOCK_BACK = "0D1D8184001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume vertical knock back. */
    protected static final String HIT_VOLUME_VERTICAL_KNOCK_BACK = "5B965C69000400000000C6415221001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume AP siphon. */
    protected static final String HIT_VOLUME_AP_SIPHON = "55F2B8EE0004000000001C36EA8300049D803EF02420FDDB001058C7BA280001DCB677300004";

    /** Hex string for setting hit volume AP generation. */
    protected static final String HIT_VOLUME_AP_GENERATION = "2576AB83001058C7BA280001DCB677300004";


    /** Hex string for setting hit reaction to bounce. */
    protected static final String BOUNCE_REACTION = "1C6017E5";

    /** Hex string for setting hit reaction to crumple. */
    protected static final String CRUMPLE_REACTION = "8320CDB7";

    /** Hex string for setting hit reaction to eject roll. */
    protected static final String EJECT_ROLL_REACTION = "E2C9BD51";

    /** Hex string for setting hit reaction to eject spiral. */
    protected static final String EJECT_SPIRAL_REACTION = "018F82BF";

    /** Hex string for setting hit reaction to eject tornado. */
    protected static final String EJECT_TORNADO_REACTION = "FD0B3D76";

    /** Hex string for setting hit reaction to full launch. */
    protected static final String FULL_LAUNCH_REACTION = "2FC92C27";

    /** Hex string for setting hit reaction to light reaction. */
    protected static final String LIGHT_REACTION_REACTION = "B0A526B0"; // C572FE3F

    /** Hex string for setting hit reaction to mini launch lift. */
    protected static final String MINI_LAUNCH_LIFT_REACTION = "DDA0DDAE";

    /** Hex string for setting hit reaction to mini launch lift sweep. */
    protected static final String MINI_LAUNCH_SWEEP_REACTION = "B05D0D35";

    /** Hex string for setting hit reaction to shock stun. */
    protected static final String SHOCK_STUN_REACTION = "E9B0D618";

    /** Hex string for setting hit reaction to slam down flatten. */
    protected static final String SLAM_DOWN_FLATTEN_REACTION = "45856983"; // Slam down bounce has same value - there must be more that sets that.

    /** Hex string for setting hit reaction to stagger buttdrop. */
    protected static final String STAGGER_BUTTDROP_REACTION = "807BBD01";

    /** Hex string for setting hit reaction to stagger kneel. */
    protected static final String STAGGER_KNEEL_REACTION = "69118031";

    /** Hex string for setting hit reaction to twitch. */
    protected static final String TWITCH_REACTION = "807BBD01";


    /** Lookup table for attribute hex values. */
    protected static final BiMap<String, String> HEX_LOOKUP_TABLE = HashBiMap.create();
    // Populate lookup table.
    static {
        // Hit Volume attributes.
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_LENGTH, "Length");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_HEIGHT, "Height");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_X_OFFSET, "X Offset");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_Y_OFFSET, "Y Offset");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_HORIZONTAL_KNOCK_BACK, "Horizontal Knock Back");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_VERTICAL_KNOCK_BACK, "Vertical Knock Back");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_AP_SIPHON, "AP Siphon");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_AP_GENERATION, "AP Generation");

        // Hit Reaction types.
        HEX_LOOKUP_TABLE.put(BOUNCE_REACTION, "Bounce");
        HEX_LOOKUP_TABLE.put(CRUMPLE_REACTION, "Crumple");
        HEX_LOOKUP_TABLE.put(EJECT_ROLL_REACTION, "Eject Roll");
        HEX_LOOKUP_TABLE.put(EJECT_SPIRAL_REACTION, "Eject Spiral");
        HEX_LOOKUP_TABLE.put(EJECT_TORNADO_REACTION, "Eject Tornado");
        HEX_LOOKUP_TABLE.put(FULL_LAUNCH_REACTION, "Full Launch");
        HEX_LOOKUP_TABLE.put(LIGHT_REACTION_REACTION, "Light Reaction");
        HEX_LOOKUP_TABLE.put(MINI_LAUNCH_LIFT_REACTION, "Mini Launch Lift");
        HEX_LOOKUP_TABLE.put(MINI_LAUNCH_SWEEP_REACTION, "Mini Launch Sweep");
        HEX_LOOKUP_TABLE.put(SHOCK_STUN_REACTION, "Shock Stun");
        HEX_LOOKUP_TABLE.put(SLAM_DOWN_FLATTEN_REACTION, "Slam Down Flatten");
        HEX_LOOKUP_TABLE.put(STAGGER_BUTTDROP_REACTION, "Stagger Buttdrop");
        HEX_LOOKUP_TABLE.put(STAGGER_KNEEL_REACTION, "Stagger Kneel");
        HEX_LOOKUP_TABLE.put(TWITCH_REACTION, "Twitch");
    }


    /** List of attributes for this function. */
    protected final ArrayList<Attribute> attributes = new ArrayList<>();

    /** The file containing the function. */
    protected final CBWS cbws;

    /** The function hex as a string. */
    protected String hex;

    /** The function label. */
    private final String label;


    /**
     * Creates a generic function instance with a function label.
     *
     * @param label The function name/label.
     * @param hex The function hex as a string.
     * @param cbws The file containing the function.
     */
    public Function(final String label, final String hex, final CBWS cbws) {
        if (!hex.startsWith("00000003"))
            throw new IllegalArgumentException(String.format(
                    "Invalid header bytes for function hex. Expected \"00000003\" but found \"%s\".",
                    hex.substring(0, 8)
            ));
        this.label = label;
        this.hex = hex;
        this.cbws = cbws;
        identifyAttributes();
    }

    /**
     * Returns the list of attributes for this function.
     *
     * @return The list of attributes.
     */
    public final ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns the function label.
     *
     * @return The function label.
     */
    public final String getLabel() {
        return label;
    }

    /**
     * Returns string representation of the function's hex.
     *
     * @return The function's hex.
     */
    public final String getHex() {
        return hex;
    }

    /**
     * Returns the list of attributes with the specified name.
     *
     * @param name The name of the attribute to search for.
     *
     * @return The list of attributes with the specified name.
     */
    public final ArrayList<Attribute> getAttributesWithName(final String name) {
        final ArrayList<Attribute> attributes = new ArrayList<>();
        for (final Attribute attribute : this.attributes) {
            if (attribute.name().equals(name)) attributes.add(attribute);
        }
        return attributes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(String.format("%s%n", label));
        for (int j = 0; j < attributes.size(); j++) {
            final Attribute attribute = attributes.get(j);
            builder.append(String.format("    %2d. %s: %s%n", j, attribute.name(), attribute.value()));
        }
        return builder.toString();
    }

    protected void identifyAttributes() {
        // Always attempt to identify numerical attributes first.
        identifyNumericalAttributes(hex);

        // Check for additional attributes on case-by-case basis.
        switch (label) {
            case "EnableHitVolume" -> identifyHitReactionType(hex);
        }
    }

    /**
     * Parses hex string to identify numerical attributes and adds them to the attributes list.
     *
     * @param hex The hex string to parse.
     */
    private void identifyNumericalAttributes(String hex) {
        while (true) {
            // Find 1st index of hex string that indicates numerical attribute is being set.
            final int startIndex;
            switch (label) {
                case "PlayRate" -> startIndex = hex.indexOf(NUMERICAL_ATTRIBUTE2);
                default -> startIndex = hex.indexOf(NUMERICAL_ATTRIBUTE1);
            }

            // If no attribute is being set, abort.
            if (startIndex < 0) return;

            // Attribute value hex will be the next four bytes.
            final int valueStartIndex;
            switch (label) {
                case "PlayRate" -> valueStartIndex = startIndex + NUMERICAL_ATTRIBUTE2.length();
                default -> valueStartIndex = startIndex + NUMERICAL_ATTRIBUTE1.length();
            }
            final int valueEndIndex = valueStartIndex + 8;

            // Attempt to identify attribute type.
            final String attributeHex = hex.substring(0, valueStartIndex);
            final String attributeType = matchNumericalAttributeHex(attributeHex);

            // Identify attribute value.
            final String valueHex = hex.substring(valueStartIndex, valueEndIndex);
            final String attributeValue = String.valueOf(getHexFloat(valueHex));

            // Add attribute.
            attributes.add(new Attribute(attributeType, attributeValue));

            // Trim string & continue parsing.
            hex = hex.substring(valueEndIndex);
        }
    }

    /**
     * Matches a hex string to a numerical attribute type.
     *
     * @param hex The hex string to match.
     *
     * @return The attribute type.
     */
    private String matchNumericalAttributeHex(final String hex) {
        // If function has one known attribute, avoid matching hex.
        switch (label) {
            case "PlayRate" -> { return "Play Rate"; }
            case "SetArmor" -> { return "Super Armor"; }
        }

        // Iterate from end of string to start of string.
        for (int i = hex.length() - 1; i >= 0; i--) {
            // Get substring from current index to end of string.
            final String substring = hex.substring(i);

            // Check lookup table for matching hex string.
            final String match = HEX_LOOKUP_TABLE.get(substring);

            // If a match was found, return the attribute type.
            if (match != null) return match;
        }

        // If hex string was exhausted without a match, return unknown attribute type.
        return UNKNOWN;
    }

    /**
     * Parses hex string to identify hit reaction type and adds it to the attributes list.
     *
     * @param hex The hex string to parse.
     */
    private void identifyHitReactionType(final String hex) {
        // Find index of hex string that indicates hit reaction is being set.
        final int startIndex = StringUtils.indexOfIgnoreCase(hex, HIT_REACTION);
        if (startIndex < 0) return;

        // Four bytes define the hit reaction. A byte is two characters
        final int reactionStartIndex = startIndex + HIT_REACTION.length();
        final int reactionEndIndex = reactionStartIndex + 8;
        final String reactionHex = hex.substring(reactionStartIndex, reactionEndIndex);

        // Add attribute for reaction type.
        final String attributeName = "Hit Reaction";
        final String reactionType = HEX_LOOKUP_TABLE.get(reactionHex);
        if (reactionType != null)
            attributes.add(new Attribute(attributeName, reactionType));
        else
            attributes.add(new Attribute(attributeName, UNKNOWN));

        // If attack causes multiple reactions, add them all.
        final String remainingHex = hex.substring(reactionEndIndex);
        identifyHitReactionType(remainingHex);
    }

    public final void modifyAttribute(final int index) {
        // Ensure index is within bounds.
        if (index < 0  || index >= attributes.size()) {
            System.out.printf("Invalid index: %d%n", index);
            return;
        }

        // Check is index is a special case.
        switch (attributes.get(index).name()) {
            case "Hit Reaction" -> modifyHitReaction(index);
            default -> modifyNumericalAttribute(index);
        }
    }

    /**
     * Modifies a numerical attribute.
     *
     * @param index The index of the attribute to modify.
     */
    private void modifyNumericalAttribute(final int index) {
        final String currentAttributeHex, newAttributeHex;
        switch (label) {
            case "PlayRate" -> {
                // Get current play rate hex.
                final String currentPlayRateValueHex = getFloatHex(Float.parseFloat(attributes.get(index).value()));
                currentAttributeHex = String.format("%s%s", NUMERICAL_ATTRIBUTE2, currentPlayRateValueHex);

                // Prompt user for new play rate value.
                final float newPlayRate = promptFloatResponse("Enter new play rate value: ");
                newAttributeHex = String.format("%s%s", NUMERICAL_ATTRIBUTE2, getFloatHex(newPlayRate));
            }
            case "SetArmor" -> {
                // Get current armor hex.
                final String currentArmorValueHex = getFloatHex(Float.parseFloat(attributes.get(index).value()));
                currentAttributeHex = String.format("%s%s", NUMERICAL_ATTRIBUTE1, currentArmorValueHex);

                // Prompt user for new armor value.
                final float newArmor = promptFloatResponse("Enter new armor value: ");
                newAttributeHex = String.format("%s%s", NUMERICAL_ATTRIBUTE1, getFloatHex(newArmor));
            }
            default -> {
                // Get reverse map of hex lookup table to match attribute names to their hex values.
                final BiMap<String, String> reverseLookupTable = HEX_LOOKUP_TABLE.inverse();

                // Get hex value for current attribute.
                final String currentAttributeName = attributes.get(index).name();
                final String currentAttributeValueHex = getFloatHex(Float.parseFloat(attributes.get(index).value()));
                currentAttributeHex = String.format("%s%s", reverseLookupTable.get(currentAttributeName), currentAttributeValueHex);

                // Prompt user to enter new value for attribute.
                System.out.printf("Current value: %s%n", attributes.get(index).value());
                final float newValue = promptFloatResponse("Enter new value: ");
                final String newAttributeValueHex = getFloatHex(newValue);
                newAttributeHex = String.format("%s%s", reverseLookupTable.get(currentAttributeName), newAttributeValueHex);
            }
        }

        cbws.replace(hex, hex.replace(currentAttributeHex, newAttributeHex));
    }

    /**
     * Modifies a hit reaction type.
     *
     * @param index The index of the attribute to modify.
     */
    private void modifyHitReaction(final int index) {
        // Get reverse map of hex lookup table to match reaction names to their hex values.
        final BiMap<String, String> reverseLookupTable = HEX_LOOKUP_TABLE.inverse();

        // Get hex value for current hit reaction.
        final String currentReactionType = attributes.get(index).value();
        final String currentReactionHex = String.format("%s%s", HIT_REACTION, reverseLookupTable.get(currentReactionType));

        // Prompt user to select a new hit reaction.
        int selection;
        final ArrayList<String> hitReactions = new ArrayList<>(HEX_LOOKUP_TABLE.values());
        while (true) {
            System.out.println("Hit Reactions:");
            for (int i = 0; i < hitReactions.size(); i++)
                System.out.printf("    %d. %s%n", i, hitReactions.get(i));
            System.out.println();
            selection = promptIntegerResponse("Enter number for hit reaction selection: ");
            if (selection < 0 || selection >= hitReactions.size()) continue;
            break;
        }

        // Get hex value for new hit reaction.
        final String newReactionType = hitReactions.get(selection);
        final String newReactionHex = String.format("%s%s", HIT_REACTION, reverseLookupTable.get(newReactionType));

        System.out.printf("Replacing current %s reaction %s with %s reaction %s%n", currentReactionType, currentReactionHex, newReactionType, newReactionHex);

        cbws.replace(hex, hex.replace(currentReactionHex, newReactionHex));
    }

    /** Removes the function from the file. */
    public void removeFunction() {
        cbws.decrementFunctionCount();
        cbws.replace(hex, "");
    }
}
