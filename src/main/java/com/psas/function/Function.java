package com.psas.function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.psas.cbws.CBWS;
import org.apache.commons.codec.DecoderException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.psas.Main.promptFloatResponse;
import static com.psas.Main.promptIntegerResponse;
import static com.psas.Main.promptYesNoResponse;
import static com.psas.cbws.CBWS.*;
import static org.apache.commons.codec.binary.Hex.decodeHex;

public class Function {
    /** Generic string for error handling. */
    protected static final String UNKNOWN = "Unknown", UNKNOWN_FUNCTION = "Unknown Function";

    /** Hex string for setting numerical attribute. */
    protected static final String NUMERICAL_ATTRIBUTE1 = "1058C7BA280001DCB677300004";

    /** Hex string for setting numerical attribute. */
    protected static final String NUMERICAL_ATTRIBUTE2 = "90070004";

    /** Hex string for setting numerical attribute. */
    protected static final String NUMERICAL_ATTRIBUTE3 = "69250004";

    /** Hex string for setting hit reaction attribute. */
    protected static final String HIT_REACTION = "48A40004";


    /** Hex string for setting custom knock back. */
    protected static final String CUSTOM_KNOCK_BACK = "39669D3A0004";
    protected static final String ENABLE_CUSTOM_KNOCK_BACK = "9BCC9A4A";


    /** Hex string for setting hit volume length. */
    protected static final String HIT_VOLUME_LENGTH_1 = "000D06D19B8400" + NUMERICAL_ATTRIBUTE1;
    protected static final String HIT_VOLUME_LENGTH_2 = "11F7129500" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume height. */
    protected static final String HIT_VOLUME_HEIGHT = "ECEE0E0C00" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hitbox angle. */
    protected static final String HITBOX_ANGLE = "D00AFAA700" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume x offset. */
    protected static final String HIT_VOLUME_X_OFFSET = "FE85D7C900" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume y offset. */
    protected static final String HIT_VOLUME_Y_OFFSET = "8982E75F00" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume horizontal knock back. */
    protected static final String HIT_VOLUME_HORIZONTAL_KNOCK_BACK = "0D1D818400" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume vertical knock back. */
    protected static final String HIT_VOLUME_VERTICAL_KNOCK_BACK = "C641522100" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume AP siphon. */
    protected static final String HIT_VOLUME_AP_SIPHON = "55F2B8EE0004000000001C36EA8300049D803EF02420FDDB00" + NUMERICAL_ATTRIBUTE1;

    /** Hex string for setting hit volume AP generation. */
    protected static final String HIT_VOLUME_AP_GENERATION = "2576AB8300";

    /** Hex string for setting hit volume AP generation default. */
    protected static final String HIT_VOLUME_AP_GENERATION_DEFAULT = "0400000000";

    /** Hex string for setting hit volume guard break. */
    protected static final String HIT_VOLUME_GUARD_BREAK = "0D940001";
    
    /** Hex string for setting projectile lifetime. */
    protected static final String PROJECTILE_LIFETIME = "7CD628C00001005CC250E5000100E7B2912B000101098D71AD000101D069FDDC0001014BA2338A000101C49152DE00" + NUMERICAL_ATTRIBUTE1;


    protected static final String BOOLEAN_ATTRIBUTE1 = "72676587D44E9E00" + NUMERICAL_ATTRIBUTE1;


    /** Hex string for setting slam down bounce enable. */
    protected static final String SLAM_DOWN_BOUNCE_ENABLE = "D04D6798";

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

    /** Hex string for setting hit reaction to generic light reaction. */
    protected static final String GENERIC_LIGHT_REACTION = "C572FE3F";

    /** Hex string for setting hit reaction to light reaction from overhead attack. */
    protected static final String LIGHT_REACTION_OVERHEAD = "B0A526B0";

    /** Hex string for setting hit reaction to mini launch lift. */
    protected static final String MINI_LAUNCH_LIFT_REACTION = "DDA0DDAE";

    /** Hex string for setting hit reaction to mini launch lift sweep. */
    protected static final String MINI_LAUNCH_SWEEP_REACTION = "B05D0D35";

    /** Hex string for setting hit reaction to shock stun. */
    protected static final String SHOCK_STUN_REACTION = "E9B0D618";

    /** Hex string for setting hit reaction to slam down flatten. */
    protected static final String SLAM_DOWN_REACTION_1 = "45856983";

    /** Hex string for setting hit reaction to stagger buttdrop. */
    protected static final String STAGGER_BUTTDROP_REACTION = "E4D46FCD";

    /** Hex string for setting hit reaction to stagger kneel. */
    protected static final String STAGGER_KNEEL_REACTION = "69118031";

    /** Hex string for setting hit reaction to twitch. */
    protected static final String TWITCH_REACTION = "CE2C3EE4";

    /** Hex string for setting no reaction. */
    protected static final String NO_REACTION = "807BBD01";

    /** Hex string for setting Jak Blaster reaction. */
    protected static final String JAK_BLASTER_REACTION = "A89843DA";

    
    /** Hex string for setting horizontal velocity. */
    protected static final String HORIZONTAL_VELOCITY_1 = "B50EE7F164D7605E001058C7BA280001DCB677300004";

    /** Hex string for setting vertical velocity. */
    protected static final String VERTICAL_VELOCITY_1 = "B50EE7F164D7605E00040000000013D050C8001058C7BA280001DCB677300004";


    /** Lookup table for attribute hex values. */
    protected static final BiMap<String, String> HEX_LOOKUP_TABLE = HashBiMap.create();
    // Populate lookup table.
    static {
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_LENGTH_1, "Hit Volume Length 1");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_LENGTH_2, "Hit Volume Length 2");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_HEIGHT, "Hit Volume Height 1");
        HEX_LOOKUP_TABLE.put(HITBOX_ANGLE, "Angle");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_X_OFFSET, "X Offset");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_Y_OFFSET, "Y Offset");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_HORIZONTAL_KNOCK_BACK, "Horizontal Knock Back");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_VERTICAL_KNOCK_BACK, "Vertical Knock Back");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_AP_SIPHON, "AP Siphon");
        HEX_LOOKUP_TABLE.put(HIT_VOLUME_AP_GENERATION + NUMERICAL_ATTRIBUTE1, "AP Generation");
        HEX_LOOKUP_TABLE.put(PROJECTILE_LIFETIME, "Projectile Lifetime (Seconds)");

        HEX_LOOKUP_TABLE.put(HORIZONTAL_VELOCITY_1, "Horizontal Velocity 1");
        HEX_LOOKUP_TABLE.put(VERTICAL_VELOCITY_1, "Vertical Velocity 1");

        HEX_LOOKUP_TABLE.put(BOOLEAN_ATTRIBUTE1, "Boolean Attribute 1");
    }

    /** Lookup table for hit reaction hex values. */
    protected static final BiMap<String, String> REACTION_LOOKUP_TABLE = HashBiMap.create();
    // Populate lookup table.
    {
        REACTION_LOOKUP_TABLE.put(BOUNCE_REACTION, "Bounce");
        REACTION_LOOKUP_TABLE.put(CRUMPLE_REACTION, "Crumple");
        REACTION_LOOKUP_TABLE.put(EJECT_ROLL_REACTION, "Eject Roll");
        REACTION_LOOKUP_TABLE.put(EJECT_SPIRAL_REACTION, "Eject Spiral");
        REACTION_LOOKUP_TABLE.put(EJECT_TORNADO_REACTION, "Eject Tornado");
        REACTION_LOOKUP_TABLE.put(FULL_LAUNCH_REACTION, "Full Launch");
        REACTION_LOOKUP_TABLE.put(LIGHT_REACTION_OVERHEAD, "Light Reaction Overhead");
        REACTION_LOOKUP_TABLE.put(GENERIC_LIGHT_REACTION, "Generic Light Reaction");
        REACTION_LOOKUP_TABLE.put(MINI_LAUNCH_LIFT_REACTION, "Mini Launch Lift");
        REACTION_LOOKUP_TABLE.put(MINI_LAUNCH_SWEEP_REACTION, "Mini Launch Sweep");
        REACTION_LOOKUP_TABLE.put(SHOCK_STUN_REACTION, "Shock Stun");
        REACTION_LOOKUP_TABLE.put(SLAM_DOWN_REACTION_1, "Slam Down");
        REACTION_LOOKUP_TABLE.put(STAGGER_BUTTDROP_REACTION, "Stagger Butt-drop");
        REACTION_LOOKUP_TABLE.put(STAGGER_KNEEL_REACTION, "Stagger Kneel");
        REACTION_LOOKUP_TABLE.put(TWITCH_REACTION, "Twitch");
        REACTION_LOOKUP_TABLE.put(NO_REACTION, "No Reaction");
        REACTION_LOOKUP_TABLE.put(JAK_BLASTER_REACTION, "Jak Blaster");
    }

    /**
     * Identifies the function label by converting the hex to ASCII and using a regex pattern to find the label.
     *
     * @param hex The hex to parse.
     *
     * @return The function label.
     */
    protected static String identifyFunctionLabel(final String hex) {
        // Initialize pattern to find function label.
        // The regex specifies that the label must contain only letters in UpperCamelCase format.
        final Pattern pattern = Pattern.compile("([A-Z][a-z]+)+");

        // Convert hex string to ASCII.
        final String ascii;
        try { ascii = new String(decodeHex(hex), StandardCharsets.UTF_8); }
        catch (final DecoderException e) {
            e.printStackTrace();
            System.exit(1);
            return UNKNOWN_FUNCTION;  // Unreachable but compiler still requires a return to us ascii var later.
        }

        // Iterate over matches until a function label is found.
        final Matcher matcher = pattern.matcher(ascii);
        while (matcher.find()) {
            // If match length is less than 4, assume it is not a function label.
            if (matcher.end() - matcher.start() < 4) continue;
            return ascii.substring(matcher.start(), matcher.end());
        }
        return UNKNOWN_FUNCTION;
    }


    /** List of attributes for this function. */
    protected final ArrayList<Attribute> attributes = new ArrayList<>();

    /** The file containing the function. */
    protected final CBWS cbws;

    /** The function hex as a string. */
    protected String hex;

    /** The function label. */
    protected final String label;

    /**
     * The frame this function will execute on. If this value does not respect the frame order in the CBWS file, it
     * will execute on the previous function's frame.
     */
    protected byte frame;

    /**
     * Creates a generic function instance with a function label.
     *
     * @param hex The function hex as a string.
     * @param cbws The file containing the function.
     */
    public Function(final String hex, final CBWS cbws) {
        this.label = identifyFunctionLabel(hex);
        this.hex = hex;
        this.cbws = cbws;
        this.frame = (byte) getHexInt(hex.substring(hex.length() - 2));
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
     * Returns the frame this function will execute on.
     *
     * @return The frame number.
     */
    public final byte getFrame() {
        return frame;
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

    /**
     * Sets the frame this function will execute on. The function hex will be updated to reflect the new frame.
     *
     * @param frame The frame number.
     */
    public void setFrame(final byte frame) {
        this.frame = frame;
        hex = hex.substring(0, hex.length() - 2) + getByteHex(frame);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(String.format("%s%n", label));
        for (int j = 0; j < attributes.size(); j++) {
            final Attribute attribute = attributes.get(j);
            builder.append(String.format("        %2d. %s: %s%n", j, attribute.name(), attribute.value()));
        }
        return builder.toString();
    }

    /** Identifies attributes for this function. */
    protected void identifyAttributes() {
        // Always attempt to identify numerical & string attributes.
        identifyNumericalAttributes();
        identifyStringAttributes();

        // Check for additional attributes on case-by-case basis.
        switch (label) {
            // Hit volumes, projectiles, & grabs
            case "EnableHitVolume", "SpawnProjectile", "UnlockPuppets" -> {
                identifyHitReactionType();
                if (!label.equals("UnlockPuppets")) identifyGuardBreak();
                if (getAttributesWithName("AP Generation").isEmpty())
                    attributes.add(new Attribute("AP Generation", "Unknown", hex.indexOf(HIT_VOLUME_AP_GENERATION) + HIT_VOLUME_AP_GENERATION.length()));
                identifyCustomKnockBack();
            }
        }

        // Sort attributes by index.
        Collections.sort(attributes);
    }

    /** Parses hex string to identify numerical attributes and adds them to the attributes list. */
    private void identifyNumericalAttributes() {
        int substringIndex = 0;

        while (true) {
            // Find 1st index of hex string that indicates numerical attribute is being set.
            final int startIndex;
            switch (label) {
                case "PlayRate" -> startIndex = hex.indexOf(NUMERICAL_ATTRIBUTE2, substringIndex);
                default -> startIndex = hex.indexOf(NUMERICAL_ATTRIBUTE1, substringIndex);
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
            attributes.add(new Attribute(attributeType, attributeValue, startIndex));

            // Trim string & continue parsing.
            substringIndex = valueEndIndex;
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
     * Parses hex string to identify hit reaction type and adds it to the attributes list. */
    private void identifyHitReactionType() {
        int substringIndex = 0;

        while (true) {
            // Find index of hex string that indicates hit reaction is being set.
            final int startIndex = hex.indexOf(HIT_REACTION, substringIndex);
            if (startIndex < 0) return;

            // Four bytes define the hit reaction. A byte is two characters
            final int reactionStartIndex = startIndex + HIT_REACTION.length();
            final int reactionEndIndex = reactionStartIndex + 8;
            final String reactionHex = hex.substring(reactionStartIndex, reactionEndIndex);

            // Add attribute for reaction type.
            final String attributeName = "Hit Reaction";
            final String reactionType = REACTION_LOOKUP_TABLE.get(reactionHex);
            if (reactionType != null) {
                if (reactionHex.equals(SLAM_DOWN_REACTION_1) || reactionHex.equals(GENERIC_LIGHT_REACTION)) {
                    // Determine slam-down bounce/flatten.
                    final int slamDownStartIndex = reactionEndIndex + 12;
                    final String slamDownHex = hex.substring(slamDownStartIndex, slamDownStartIndex + 8);
                    if (slamDownHex.equals("00000000")  && !reactionHex.equals(GENERIC_LIGHT_REACTION))
                        attributes.add(new Attribute(attributeName, reactionType + " - Flatten", startIndex));
                    else if (!reactionHex.equals(GENERIC_LIGHT_REACTION))
                        attributes.add(new Attribute(attributeName, reactionType + " - Bounce", startIndex));
                    else attributes.add(new Attribute(attributeName, reactionType, startIndex));
                }
                else attributes.add(new Attribute(attributeName, reactionType, startIndex));
            }
            else
                attributes.add(new Attribute(attributeName, UNKNOWN, startIndex));

            // If attack causes multiple reactions, add them all.
            substringIndex = reactionEndIndex;
        }
    }

    private void identifyCustomKnockBack() {
        final int startIndex = hex.indexOf(CUSTOM_KNOCK_BACK) + CUSTOM_KNOCK_BACK.length();
        final String currentCustomKnockBackHex = hex.substring(startIndex, startIndex + 8);
        if (currentCustomKnockBackHex.equals(ENABLE_CUSTOM_KNOCK_BACK))
            attributes.add(new Attribute("Custom Knock Back", "True", startIndex));
        else attributes.add(new Attribute("Custom Knock Back", "False", startIndex));
    }

    private void identifyStringAttributes() {
        // Initialize pattern to find string values.
        final Pattern pattern = Pattern.compile("([A-Z][a-z]+|[A-Z]+|[a-z]+|_|[0-9]+| +|/+|\\*+)+");

        // Convert hex string to ASCII.
        final String ascii;
        try { ascii = new String(decodeHex(hex), StandardCharsets.UTF_8); }
        catch (final DecoderException e) {
            e.printStackTrace();
            System.exit(1);
            return;  // Unreachable but compiler still requires a return to use ascii var later.
        }

        final Matcher matcher = pattern.matcher(ascii);
        switch (label) {
            default -> {
                // Iterate over matches until a function label is found.
                int matchCount = 0;
                while (matcher.find()) {
                    // If match length is less than 5, assume it is not a function label.
                    if (matcher.end() - matcher.start() < 5) continue;
                    final String match = ascii.substring(matcher.start(), matcher.end());
                    matchCount++;
                    if (!match.equals(label))
                        attributes.add(new Attribute(String.format("String Attribute %d", matchCount - 1), match, matcher.start()));
                }

            }
        }
    }

    /** Identifies guard break property. This is only relevant for hit volumes. */
    private void identifyGuardBreak() {
        final int startIndex = hex.indexOf(HIT_VOLUME_GUARD_BREAK) + HIT_VOLUME_GUARD_BREAK.length();
        final String currentGuardBreakHex = hex.substring(startIndex, startIndex + 2);
        if (currentGuardBreakHex.equals("01")) attributes.add(new Attribute("Guard Break", "True", startIndex));
        else attributes.add(new Attribute("Guard Break", "False", startIndex));
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
            case "Guard Break" -> modifyGuardBreak();
            case "AP Generation" -> modifyAPGeneration(index);
            default -> modifyNumericalAttribute(index);
        }

        // Update attributes list.
        attributes.clear();
        identifyAttributes();
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

        hex = hex.replaceFirst(currentAttributeHex, newAttributeHex);
    }

    /**
     * Modifies a hit reaction type.
     *
     * @param index The index of the attribute to modify.
     */
    private void modifyHitReaction(final int index) {
        // Get reverse map of hex lookup table to match reaction names to their hex values.
        final BiMap<String, String> reverseLookupTable = REACTION_LOOKUP_TABLE.inverse();

        // Get hex value for current hit reaction.
        final String currentReactionType = attributes.get(index).value();
        final String currentReactionHex = String.format("%s%s", HIT_REACTION, reverseLookupTable.get(currentReactionType));

        // Prompt user to select a new hit reaction.
        int selection;
        final ArrayList<String> hitReactions = new ArrayList<>(REACTION_LOOKUP_TABLE.values());
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
        if (newReactionType.equals("Slam Down") || newReactionType.equals("Generic Light Reaction")) {
            // Determine slam-down bounce/flatten.
            final boolean bounce = promptYesNoResponse("Enable bounce?");

            // Four bytes define the hit reaction. A byte is two characters
            final int startIndex = attributes.get(index).index();
            final int reactionStartIndex = startIndex + HIT_REACTION.length();
            final int reactionEndIndex = reactionStartIndex + 8;
            final int slamDownStartIndex = reactionEndIndex + 12;
            if (bounce)
                hex = hex.substring(0, slamDownStartIndex) + SLAM_DOWN_BOUNCE_ENABLE + hex.substring(slamDownStartIndex + 8);
            else
                hex = hex.substring(0, slamDownStartIndex) + "00000000" + hex.substring(slamDownStartIndex + 8);
        }
        final String newReactionHex = String.format("%s%s", HIT_REACTION, reverseLookupTable.get(newReactionType));

        hex = hex.replaceFirst(currentReactionHex, newReactionHex);
    }

    private void modifyGuardBreak() {
        final int startIndex = getAttributesWithName("Guard Break").get(0).index();
        final boolean enable = promptYesNoResponse("Enable guard break?");
        if (enable)
            hex = hex.substring(0, startIndex) + "01" + hex.substring(startIndex + 2);
        else
            hex = hex.substring(0, startIndex) + "00" + hex.substring(startIndex + 2);
    }

    private void modifyAPGeneration(final int index) {
        if (!getAttributesWithName("AP Generation").get(0).value().equals("Unknown")) {
            modifyNumericalAttribute(index);
            return;
        }
        final float newValue = promptFloatResponse("Enter new value: ");
        final String newAttributeValueHex = getFloatHex(newValue);
        final int startIndex = hex.indexOf(HIT_VOLUME_AP_GENERATION) + HIT_VOLUME_AP_GENERATION.length();
        if (!hex.startsWith(HIT_VOLUME_AP_GENERATION_DEFAULT, startIndex)) {
            System.out.println("Unexpected byte sequence found. Unable to modify AP generation.");
            return;
        }
        hex = hex.replaceFirst(
                HIT_VOLUME_AP_GENERATION + HIT_VOLUME_AP_GENERATION_DEFAULT,
                HIT_VOLUME_AP_GENERATION + NUMERICAL_ATTRIBUTE1 + newAttributeValueHex
        );
    }
}
