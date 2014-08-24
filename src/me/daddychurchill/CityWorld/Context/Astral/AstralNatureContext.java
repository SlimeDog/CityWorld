package me.daddychurchill.CityWorld.Context.Astral;

import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Context.NatureContext;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Plats.Astral.AstralNatureLot;
import me.daddychurchill.CityWorld.Plats.Astral.AstralShipLot;
import me.daddychurchill.CityWorld.Plugins.ShapeProvider;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.PlatMap;

public class AstralNatureContext extends NatureContext {
	
	public AstralNatureContext(WorldGenerator generator) {
		super(generator);
		
		oddsOfIsolatedConstructs = Odds.oddsPrettyUnlikely;
	}
	
	@Override
	public PlatLot createNaturalLot(WorldGenerator generator, PlatMap platmap, int x, int z) {
		return new AstralNatureLot(platmap, platmap.originX + x, platmap.originZ + z);
	}
	
	@Override
	public void populateMap(WorldGenerator generator, PlatMap platmap) {
		
		//TODO, This doesn't handle schematics quite right yet
		// let the user add their stuff first, then plug any remaining holes with our stuff
		//mapsSchematics.populate(generator, platmap);
		
		// random fluff
		ShapeProvider shapeProvider = generator.shapeProvider;
		
		// where it all begins
		int originX = platmap.originX;
		int originZ = platmap.originZ;
		
		// is this natural or buildable?
		for (int x = 0; x < PlatMap.Width; x++) {
			for (int z = 0; z < PlatMap.Width; z++) {
				PlatLot current = platmap.getLot(x, z);
				if (current == null) {
					
					// get the height info for this chunk
					if (shapeProvider.isIsolatedConstructAt(originX + x, originZ + z, oddsOfIsolatedConstructs))
						current = new AstralShipLot(platmap, originX + x, originZ + z);

					// did current get defined?
					if (current != null)
						platmap.setLot(x, z, current);
				}
			}
		}
	}
}
