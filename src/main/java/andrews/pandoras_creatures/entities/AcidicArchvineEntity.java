package andrews.pandoras_creatures.entities;

import javax.annotation.Nullable;

import andrews.pandoras_creatures.entities.goals.acidic_archvine.TargetUnderneathGoal;
import andrews.pandoras_creatures.registry.PCEntities;
import andrews.pandoras_creatures.registry.PCItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AcidicArchvineEntity extends MonsterEntity
{
//	private static Biome[] biomes = new Biome[] {Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE};
	private static final DataParameter<Integer> ARCHVINE_TYPE = EntityDataManager.createKey(AcidicArchvineEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(AcidicArchvineEntity.class, DataSerializers.VARINT);
	private LivingEntity targetedEntity;
	private int attackState;
	
    public AcidicArchvineEntity(EntityType<? extends AcidicArchvineEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    public AcidicArchvineEntity(World world, double posX, double posY, double posZ)
    {
        this(PCEntities.ACIDIC_ARCHVINE.get(), world);
        this.setPosition(posX, posY, posZ);
    }

    @Override
    protected void registerGoals()
    {
    	this.targetSelector.addGoal(1, new TargetUnderneathGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
    }
    
    @Override
    protected void registerData()
    {
    	super.registerData();
        this.dataManager.register(TARGET_ENTITY, 0);
        this.dataManager.register(ARCHVINE_TYPE, 0);
    }
    
    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
    	return new ItemStack(PCItems.ACIDIC_ARCHVINE_SPAWN_EGG.get());
    }
    
    @Override
	public void writeAdditional(CompoundNBT compound)
    {
		super.writeAdditional(compound);
		compound.putInt("ArchvineType", this.getArchvineType());
	}
	
	@Override
	public void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		this.setArchvineType(compound.getInt("ArchvineType"));
	}
    
    public void setTargetedEntity(int entityId)
    {
    	this.dataManager.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity()
    {
    	return this.dataManager.get(TARGET_ENTITY) != 0;
    }

    @Nullable
    public LivingEntity getTargetedEntity()
    {
    	if(!this.hasTargetedEntity())
    	{
    		return null;
    	}
    	else if(this.world.isRemote)
    	{
    		if(this.targetedEntity != null)
    		{
    			return this.targetedEntity;
    		} 
    		else
    		{
    			Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
    			if(entity instanceof LivingEntity)
    			{
    				this.targetedEntity = (LivingEntity)entity;
    				return this.targetedEntity;
    			}
    			else
    			{
    				return null;
    			}
    		}
    	}
    	else
    	{
    		return this.getAttackTarget();
    	}
    }
    
    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
    	float damage = (float)(4 + this.rand.nextInt(3));
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        return flag;
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
    	super.notifyDataManagerChange(key);
    	if(TARGET_ENTITY.equals(key))
    	{
    		this.targetedEntity = null;
    	}
    }
    
    //Moves the plant up or down by half a Block, depending on the position it got placed at.
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag)
    {	
    	spawnDataIn = super.onInitialSpawn(world, difficultyIn, reason, spawnDataIn, dataTag);
    	int type = this.getTypeForBiome(worldIn);
    	if(dataTag != null && dataTag.contains("ArchvineType", 3))
		{
			this.setArchvineType(dataTag.getInt("ArchvineType"));
			return spawnDataIn;
		}
		this.setArchvineType(type);
    	
    	if((worldIn.getBlockState(new BlockPos(this).up(2)).getBlock() != Blocks.JUNGLE_LEAVES && worldIn.getBlockState(new BlockPos(this).up(2)).getBlock() != Blocks.NETHERRACK) && (worldIn.getBlockState(new BlockPos(this).up()).getBlock() != Blocks.JUNGLE_LEAVES && worldIn.getBlockState(new BlockPos(this).up()).getBlock() != Blocks.NETHERRACK))
    	{
    		this.damageEntity(DamageSource.CRAMMING, Float.MAX_VALUE);
    	}
    	else if(worldIn.getBlockState(new BlockPos(this).up()).getBlock() == Blocks.JUNGLE_LEAVES || worldIn.getBlockState(new BlockPos(this).up()).getBlock() == Blocks.NETHERRACK)
    	{
    		this.setPosition(this.posX, this.posY - 0.5, this.posZ);
    	}
    	else if(worldIn.getBlockState(new BlockPos(this).up()).getBlock() == Blocks.AIR && (worldIn.getBlockState(new BlockPos(this).up(2)).getBlock() == Blocks.JUNGLE_LEAVES || worldIn.getBlockState(new BlockPos(this).up(2)).getBlock() == Blocks.NETHERRACK))
    	{
    		this.setPosition(this.posX, this.posY + 0.5, this.posZ);
    	}
    	
    	return spawnDataIn;
    }
    
    private int getTypeForBiome(IWorld world)
    {
		Biome biome = world.getBiome(new BlockPos(this));
		if(biome == Biomes.NETHER)
		{
			return 2;
		}
		return 1;
	}
    
    @Override
    public void tick()
    {
    	super.tick();
    	if(this.getEntityWorld().getBlockState(new BlockPos(this).up(2)).getBlock().equals(Blocks.JUNGLE_LEAVES) || this.getEntityWorld().getBlockState(new BlockPos(this).up(2)).getBlock().equals(Blocks.NETHERRACK))
    	{
    		this.setMotion(Vec3d.ZERO);
    	}
    }
    
    @Override
    public void onDeath(DamageSource cause)
    {
    	super.onDeath(cause);
    	if(this.getAttackTarget() != null)
    	{
    		this.getAttackTarget().setNoGravity(false);
    		if(this.getAttackTarget() instanceof PlayerEntity)
    		{
    			PlayerEntity player = (PlayerEntity) this.getAttackTarget();
    			if(!player.isCreative())
    			{
    				player.abilities.allowFlying = false;
    			}
    		}
    	}
    }
    
    @Override
    public boolean canBePushed()
    {
    	return false;
    }

    @Override
    public void collideWithEntity(Entity entityIn) {}

    @Override
    public void collideWithNearbyEntities() {}

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }
    
    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
    {
    	return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id)
    {
    	if(id == 4)
    	{
    		this.attackState = 0;
    	}
    	else if(id == 5)
    	{
    		this.attackState = 1;
    	}
    	else if(id == 6)
    	{
    		this.attackState = 2;
    	}
    	else
    	{
    		super.handleStatusUpdate(id);
    	}
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getAttackState()
    {
       return this.attackState;
    }
    
    public int getArchvineType()
    {
    	if(this.dataManager.get(ARCHVINE_TYPE) == 0)
    	{
    		this.dataManager.set(ARCHVINE_TYPE, 1);
    		return this.dataManager.get(ARCHVINE_TYPE);
    	}
    	else
    	{
    		return this.dataManager.get(ARCHVINE_TYPE);
    	}
	}
	
	public void setArchvineType(int typeId)
	{
		this.dataManager.set(ARCHVINE_TYPE, typeId);
	}
    
    public void setAttackState(int value)
    {
       this.attackState = value;
    }
    
//    public static void addSpawn()
//    {
//		ForgeRegistries.BIOMES.getValues().stream().forEach(AcidicArchvineEntity::processSpawning);
//	}
//	
//    private static void processSpawning(Biome biome)
//    {
//		if(Arrays.asList(biomes).contains(biome))
//		{
//			biome.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(PCEntities.ACIDIC_ARCHVINE.get(), 100, 1, 1));
//        }
//		else if(biome == Biomes.NETHER)
//		{
//			biome.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(PCEntities.ACIDIC_ARCHVINE.get(), 30, 1, 1));
//		}
//	}
}
