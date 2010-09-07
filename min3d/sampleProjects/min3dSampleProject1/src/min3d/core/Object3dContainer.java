package min3d.core;

import java.util.ArrayList;

import min3d.interfaces.IObject3dContainer;

public class Object3dContainer extends Object3d implements IObject3dContainer
{
	protected ArrayList<Object3d> _children = new ArrayList<Object3d>();

	/**
	 * Adds container functionality to Object3d.
	 * 
	 * Subclass Object3dContainer instead of Object3d if you
	 * believe you may want to add children to that object. 
	 */
	public Object3dContainer(int $maxVerts, int $maxFaces)
	{
		super($maxVerts, $maxFaces, true,true,true);
	}

	public Object3dContainer(int $maxVerts, int $maxFaces,  Boolean $useUvs, Boolean $useNormals, Boolean $useVertexColors)
	{
		super($maxVerts, $maxFaces, $useUvs,$useNormals,$useVertexColors);
	}
	
	/**
	 * This constructor is convenient for cloning purposes 
	 */
	public Object3dContainer(Vertices $vertices, FacesBufferedList $faces, TextureList $textures)
	{
		super($vertices, $faces, $textures);
	}
	
	@Override
	public void addChild(Object3d $o)
	{
		_children.add($o);
		
		$o.parent(this);
		$o.scene(this.scene());
	}
	
	@Override
	public void addChildAt(Object3d $o, int $index) 
	{
		_children.add($index, $o);
		
		$o.parent(this);
		$o.scene(this.scene());
	}

	@Override
	public boolean removeChild(Object3d $o)
	{
		boolean b = _children.remove($o);
		
		if (b) {
			$o.parent(null);
			$o.scene(null);
		}
		return b;
	}
	
	@Override
	public Object3d removeChildAt(int $index) 
	{
		Object3d o = _children.remove($index);
		if (o != null) {
			o.parent(null);
			o.scene(null);
		}
		return o;
	}
	
	@Override
	public Object3d getChildAt(int $index) 
	{
		return _children.get($index);
	}

	/**
	 * TODO: Use better lookup 
	 */
	@Override
	public Object3d getChildByName(String $name)
	{
		for (int i = 0; i < _children.size(); i++)
		{
			if (_children.get(i).name().equals($name)) return _children.get(i); 
		}
		return null;
	}

	@Override
	public int getChildIndexOf(Object3d $o) 
	{
		return _children.indexOf($o);		
	}


	@Override
	public int numChildren() 
	{
		return _children.size();
	}
	
	/*package-private*/ 
	ArrayList<Object3d> children()
	{
		return _children;
	}
	
	public Object3dContainer clone()
	{
		Vertices v = _vertices.clone();
		FacesBufferedList f = _faces.clone();
			
		Object3dContainer clone = new Object3dContainer(v, f, _textures);
		clone.position().x = position().x;
		clone.position().y = position().y;
		clone.position().z = position().z;
		clone.rotation().x = rotation().x;
		clone.rotation().y = rotation().y;
		clone.rotation().z = rotation().z;
		clone.scale().x = scale().x;
		clone.scale().y = scale().y;
		clone.scale().z = scale().z;
		return clone;
	}

}
