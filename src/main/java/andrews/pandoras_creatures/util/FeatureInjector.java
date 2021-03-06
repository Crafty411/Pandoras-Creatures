package andrews.pandoras_creatures.util;

import andrews.pandoras_creatures.registry.PCBlocks;
import andrews.pandoras_creatures.world.PCFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.GrassFeatureConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;

public class FeatureInjector
{
	/**
	 * Adds features to Vanilla Biomes
	 */
	public static void addFeaturesToBiomes()
	{
		Biomes.PLAINS.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(PCFeatures.HORSETAIL.get(), new GrassFeatureConfig(PCBlocks.HORSETAIL.get().getDefaultState()), Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(2)));
		Biomes.SUNFLOWER_PLAINS.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(PCFeatures.HORSETAIL.get(), new GrassFeatureConfig(PCBlocks.HORSETAIL.get().getDefaultState()), Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(1)));
		Biomes.SWAMP.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(PCFeatures.DHANIA.get(), new GrassFeatureConfig(PCBlocks.DHANIA.get().getDefaultState()), Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(1)));
		Biomes.MOUNTAINS.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(PCFeatures.HILL_BLOOM.get(), new GrassFeatureConfig(PCBlocks.HILL_BLOOM.get().getDefaultState()), Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(1)));
	}
}
