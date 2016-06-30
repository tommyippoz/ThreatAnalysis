/**
 * 
 */
package ippoz.irene.threatanalysis.component;

/**
 * @author Tommy
 *
 */
public abstract class Component {
	
	private String name;
	private ComponentType compType;
	
	public Component(String name, ComponentType compType) {
		this.name = name;
		this.compType = compType;
	}

	public String getName() {
		return name;
	}

	public ComponentType getCompType() {
		return compType;
	}

	public static ComponentCategory getCategoryOf(ComponentType cType) {
		switch(cType){
			case CP:
			case AP:
			case DES:
			case F:
			case H:
			case LRC:
			case O:
			case OD:
			case PS:
			case S:
			case SB:
			case SH:
				return ComponentCategory.BLD;
			case BDC:
			case SCADA:
				return ComponentCategory.DAC;
			case DC:
			case EC:
			case MG:
				return ComponentCategory.CON;
			case PP:
			case PVG:
			case WF:
				return ComponentCategory.EP;
			default:
				return null;
		}
	}

	@Override
	public abstract String toString();
	
}
