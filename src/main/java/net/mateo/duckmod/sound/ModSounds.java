package net.mateo.duckmod.sound;

import net.mateo.duckmod.DuckMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DuckMod.MOD_ID);

    // Female duck quacks
    public static final RegistryObject<SoundEvent> FEMALE_QUACK_1 =
            registerSoundEvent("female_quack_1");
    public static final RegistryObject<SoundEvent> FEMALE_QUACK_2 =
            registerSoundEvent("female_quack_2");

    // Male duck quacks
    public static final RegistryObject<SoundEvent> MALE_QUACK_1 =
            registerSoundEvent("male_quack_1");
    public static final RegistryObject<SoundEvent> MALE_QUACK_2 =
            registerSoundEvent("male_quack_2");
    public static final RegistryObject<SoundEvent> MALE_QUACK_3 =
            registerSoundEvent("male_quack_3");

    // Duck hiss
    public static final RegistryObject<SoundEvent> DUCK_HISS =
            registerSoundEvent("duck_hiss");

    // Duck eating
    public static final RegistryObject<SoundEvent> DUCK_EATING =
            registerSoundEvent("duck_eating");

    // Hit/hurt sounds
    public static final RegistryObject<SoundEvent> HIT_FEMALE_DUCK =
            registerSoundEvent("hit_female_duck");
    public static final RegistryObject<SoundEvent> HIT_MALE_DUCK =
            registerSoundEvent("hit_male_duck");

    // Death sounds
    public static final RegistryObject<SoundEvent> DEATH_FEMALE_DUCK_1 =
            registerSoundEvent("death_female_duck_1");
    public static final RegistryObject<SoundEvent> DEATH_FEMALE_DUCK_2 =
            registerSoundEvent("death_female_duck_2");
    public static final RegistryObject<SoundEvent> DEATH_MALE_DUCK_1 =
            registerSoundEvent("death_male_duck_1");
    public static final RegistryObject<SoundEvent> DEATH_MALE_DUCK_2 =
            registerSoundEvent("death_male_duck_2");
    public static final RegistryObject<SoundEvent> RARE_DEATH_SOUND =  // Easter egg
            registerSoundEvent("rare_deathsound");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, name)
                )
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}