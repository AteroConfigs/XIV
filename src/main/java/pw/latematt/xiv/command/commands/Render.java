package pw.latematt.xiv.command.commands;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Render implements CommandHandler {
    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                case "linewidth":
                case "lw":
                    if (arguments.length >= 3) {
                        Value<Float> lineWidth = (Value<Float>) XIV.getInstance().getValueManager().find("render_line_width");
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            lineWidth.setValue(lineWidth.getDefault());
                        } else {
                            Float width = Float.parseFloat(arguments[2]);
                            lineWidth.setValue(width);
                        }
                        ChatLogger.print(String.format("Render Line Width set to: %s", lineWidth.getValue()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: linewidth <float>");
                    }
                    break;
                case "antialiasing":
                case "aa":
                    Value<Boolean> antiAliasing = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_anti_aliasing");
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            antiAliasing.setValue(antiAliasing.getDefault());
                        } else {
                            antiAliasing.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        antiAliasing.setValue(!antiAliasing.getValue());
                    }
                    ChatLogger.print(String.format("Render mods will %s use antialiasing.", antiAliasing.getValue() ? "now" : "no longer"));
                    break;
                case "worldbobbing":
                case "wb":
                    Value<Boolean> worldBobbing = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_world_bobbing");
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            worldBobbing.setValue(worldBobbing.getDefault());
                        } else {
                            worldBobbing.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        worldBobbing.setValue(!worldBobbing.getValue());
                    }
                    ChatLogger.print(String.format("Render mods will %s render world bobbing.", worldBobbing.getValue() ? "now" : "no longer"));
                    break;
                case "tracerentity":
                case "te":
                    Value<Boolean> tracerEntity = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_tracer_entity");
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            tracerEntity.setValue(tracerEntity.getDefault());
                        } else {
                            tracerEntity.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        tracerEntity.setValue(!tracerEntity.getValue());
                    }
                    ChatLogger.print(String.format("Render mods will %s start from tracer entity.", tracerEntity.getValue() ? "no longer" : "now"));
                    break;
                case "nametagopacity":
                case "nto":
                    Value<Double> nametagOpacity = (Value<Double>) XIV.getInstance().getValueManager().find("render_nametag_opacity");
                    if (arguments.length >= 3) {
                        String newNametagOpacityString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                nametagOpacity.setValue(nametagOpacity.getDefault());
                            } else {
                                double newNametagOpacity = Double.parseDouble(newNametagOpacityString);
                                if (newNametagOpacity > 1.0F) {
                                    newNametagOpacity = 1.0F;
                                } else if (newNametagOpacity < 0.1F) {
                                    newNametagOpacity = 0.1F;
                                }
                                nametagOpacity.setValue(newNametagOpacity);
                            }
                            ChatLogger.print(String.format("Render mod nametag opacity set to %s", nametagOpacity.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newNametagOpacityString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: render nametagopacity <number>");
                    }
                    break;
                case "nametagsize":
                case "nts":
                    Value<Double> nametagSize = (Value<Double>) XIV.getInstance().getValueManager().find("render_nametag_size");
                    if (arguments.length >= 3) {
                        String newNametagSizeString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                nametagSize.setValue(nametagSize.getDefault());
                            } else {
                                double newNametagSize = Double.parseDouble(newNametagSizeString);
                                if (newNametagSize > 10.0F) {
                                    newNametagSize = 10.0F;
                                } else if (newNametagSize < 0.1F) {
                                    newNametagSize = 0.1F;
                                }
                                nametagSize.setValue(newNametagSize);
                            }
                            ChatLogger.print(String.format("Render mod nametag size set to %s", nametagSize.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newNametagSizeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: render nametagsize <number>");
                    }
                    break;
                case "showtags":
                case "st":
                    Value<Boolean> showTags = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_show_tags");
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            showTags.setValue(showTags.getDefault());
                        } else {
                            showTags.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        showTags.setValue(!showTags.getValue());
                    }
                    ChatLogger.print(String.format("ArrayList will %s show mod tags.", showTags.getValue() ? "now" : "no longer"));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: linewidth, antialiasing, worldbobbing, nametagsize, nametagopacity, tracerentity, showtags");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: render <action>");
        }
    }
}
