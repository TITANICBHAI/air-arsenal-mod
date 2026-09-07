package com.airarsenal.item;

import com.airarsenal.creativetab.AirArsenalTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemFieldManual extends Item {

    public ItemFieldManual() {
        setRegistryName("field_manual");
        setTranslationKey("airarsenal.field_manual");
        setCreativeTab(AirArsenalTab.INSTANCE);
        setMaxStackSize(1);
    }

    /**
     * Generates a signed vanilla Written Book (Items.WRITTEN_BOOK)
     * authored by TBTechs with comprehensive flight and combat instructions.
     */
    public static ItemStack createSignedBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("author", "TBTechs");
        tag.setString("title", "TBTechs Field Manual");
        tag.setInteger("generation", 0); // Original copy

        NBTTagList pages = new NBTTagList();

        // Page 1: Title & Introduction
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_BLUE + "" + TextFormatting.BOLD + "AIR ARSENAL\n" +
            TextFormatting.GOLD + "" + TextFormatting.BOLD + "OPERATOR MANUAL\n\n" +
            TextFormatting.DARK_GRAY + "Signed: " + TextFormatting.BLACK + "TBTechs\n" +
            TextFormatting.DARK_GRAY + "Version: " + TextFormatting.DARK_GREEN + "1.12.2-R1\n\n" +
            TextFormatting.BLACK + "Attention Operator:\n" +
            "Do not attempt to fly or crew these military combat platforms blind.\n\n" +
            "Read each section carefully for flight keys, weapon systems, and drone links."
        ));

        // Page 2: Flight Controls & Physics
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_RED + "" + TextFormatting.BOLD + "FLIGHT CONTROLS\n\n" +
            TextFormatting.BLACK + "Applies to: Biplane, Monoplane, Fighter Jet & Stealth Bomber.\n\n" +
            TextFormatting.BLUE + "W / S: " + TextFormatting.BLACK + "Pitch Down / Up\n" +
            TextFormatting.BLUE + "A / D: " + TextFormatting.BLACK + "Roll Left / Right\n" +
            TextFormatting.BLUE + "Space: " + TextFormatting.BLACK + "Increase Throttle\n" +
            TextFormatting.BLUE + "Left Shift: " + TextFormatting.BLACK + "Brake / Throttle Down\n\n" +
            TextFormatting.DARK_RED + "STALL WARNING: " + TextFormatting.BLACK + "Maintain airspeed above stall threshold or lift will fail."
        ));

        // Page 3: Predator Drone UAV
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_BLUE + "" + TextFormatting.BOLD + "PREDATOR DRONE\n\n" +
            TextFormatting.BLACK + "You do " + TextFormatting.BOLD + "NOT" + TextFormatting.RESET + " sit in the drone.\n\n" +
            TextFormatting.DARK_GREEN + "1. Hold Drone Controller\n" +
            "2. Right-click Predator Drone to link SATCOM.\n" +
            "3. Your view shifts to the drone's optical gimbal.\n" +
            "4. Steer remotely and fire Hellfire missiles.\n" +
            "5. Press Shift to disconnect camera."
        ));

        // Page 4: Stealth Bomber
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_PURPLE + "" + TextFormatting.BOLD + "STEALTH BOMBER\n\n" +
            TextFormatting.BLACK + "Heavy high-altitude penetrator.\n\n" +
            TextFormatting.DARK_BLUE + "Key [G]: " + TextFormatting.BLACK + "Toggle Stealth Cloak.\n" +
            "While cloaked, radar and automated AA Flak cannot target you.\n\n" +
            TextFormatting.RED + "Notice: " + TextFormatting.BLACK + "Stealth consumes 2x jet fuel. Keep fuel reserves topped up."
        ));

        // Page 5: Combat Vehicles
        pages.appendTag(new NBTTagString(
            TextFormatting.GOLD + "" + TextFormatting.BOLD + "GROUND VEHICLES\n\n" +
            TextFormatting.BLACK + TextFormatting.BOLD + "Main Battle Tank:\n" +
            TextFormatting.BLACK + "Auto-targets hostiles within 30 blocks. Chobham armor absorbs rockets.\n\n" +
            TextFormatting.BLACK + TextFormatting.BOLD + "Missile Truck:\n" +
            TextFormatting.BLACK + "W/S drives, A/D steers. Fires TV-guided cruise missiles that transfer camera view to warhead until hit."
        ));

        // Page 6: Artillery & Munitions
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_GREEN + "" + TextFormatting.BOLD + "ARTILLERY & AA\n\n" +
            TextFormatting.BLACK + "- " + TextFormatting.BOLD + "Howitzer: " + TextFormatting.RESET + "Set target coordinates in GUI.\n" +
            "- " + TextFormatting.BOLD + "Mortar: " + TextFormatting.RESET + "Dial angle & range.\n" +
            "- " + TextFormatting.BOLD + "AA & Flak: " + TextFormatting.RESET + "Autonomous proximity blast against aircraft.\n" +
            "- " + TextFormatting.BOLD + "Orbital Cannon: " + TextFormatting.RESET + "Requires Satellite Card & Designator."
        ));

        // Page 7: Default Keybindings
        pages.appendTag(new NBTTagString(
            TextFormatting.DARK_BLUE + "" + TextFormatting.BOLD + "KEY CHEATSHEET\n\n" +
            TextFormatting.BLUE + "G: " + TextFormatting.BLACK + "Afterburner / Stealth\n" +
            TextFormatting.BLUE + "Z: " + TextFormatting.BLACK + "Strafe Left (Helo)\n" +
            TextFormatting.BLUE + "C: " + TextFormatting.BLACK + "Strafe Right (Helo)\n" +
            TextFormatting.BLUE + "L-Click: " + TextFormatting.BLACK + "Fire Guns/Weapons\n" +
            TextFormatting.BLUE + "R-Click: " + TextFormatting.BLACK + "Board / Open GUI\n" +
            TextFormatting.BLUE + "Shift: " + TextFormatting.BLACK + "Dismount / Unlink\n\n" +
            TextFormatting.DARK_GRAY + "Safe hunting,\n" +
            TextFormatting.BLACK + "" + TextFormatting.BOLD + "- TBTechs"
        ));

        tag.setTag("pages", pages);
        book.setTagCompound(tag);
        return book;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            ItemStack signedBook = createSignedBook();
            if (!playerIn.inventory.addItemStackToInventory(signedBook)) {
                playerIn.dropItem(signedBook, false);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }
}
