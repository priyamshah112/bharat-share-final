package com.softglu.bharatshare.view;

import com.softglu.bharatshare.fragment.EditableListFragment;
import com.softglu.bharatshare.widget.EditableListAdapter;

public interface EditableListFragmentModelImpl<V extends EditableListAdapter.EditableViewHolder>
{
    void setLayoutClickListener(EditableListFragment.LayoutClickListener<V> clickListener);
}
